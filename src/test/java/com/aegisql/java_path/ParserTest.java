package com.aegisql.java_path;

import com.aegisql.java_path.parser.*;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ParserTest {

    List<TypedPathElement> pathList;

    @Before
    public void init() {
        pathList = new LinkedList<>();
    }

    static class Visitor implements CCJavaPathParserVisitor {
        @Override
        public Object visit(SimpleNode node, Object data) {
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTfullPath node, Object data) {
            LinkedList<TypedPathElement> pathList = (LinkedList<TypedPathElement>) data;
            TypedPathElement typedPathElement = new TypedPathElement();
            pathList.add(typedPathElement);
            Object value = node.jjtGetValue();
            if(value != null) {
                typedPathElement.setType(value.toString());
            }
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTpathElement node, Object data) {
            LinkedList<TypedPathElement> pathList = (LinkedList<TypedPathElement>) data;
            pathList.getLast().setName(node.jjtGetValue().toString());
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTparameters node, Object data) {
            LinkedList<TypedPathElement> pathList = (LinkedList<TypedPathElement>) data;
            pathList.getLast().addParameter((TypedValue) node.jjtGetValue());
            return node.childrenAccept(this,data);
        }

    }

    @Test(expected = JavaPathRuntimeException.class)
    public void parseVoidTestShouldFail() throws ParseException {
        testPattern("");
    }

    @Test
    public void parseSingleLabelTest() throws ParseException {
        testPattern("a");
    }

    @Test
    public void parseSingleLabelInParenthTest() throws ParseException {
        testPattern("(a)");
    }

    @Test
    public void parseSingleLabelInParenthWithTypeTest() throws ParseException {
        testPattern("(x.y a)");
    }

    @Test(expected = JavaPathRuntimeException.class)
    public void parseSingleLabelNoParenthWithTypeTestShouldFail() throws ParseException {
        testPattern("x.y a");
    }

    @Test
    public void parseSingleLabelWithParamTest() throws ParseException {
        testPattern("a{b}");
    }

    @Test
    public void parseSingleLabelWithUnicodeParamTest() throws ParseException {
        testPattern("a{'ПРОВЕРКА'}");
    }

    @Test(expected = TokenMgrException.class)
    public void parseSingleLabelWithUnicodeParamFailingTest() throws ParseException {
        testPattern("a{ПРОВЕРКА}");
    }

    @Test
    public void parseSingleLabelWithSingleQuotedParamTest() throws ParseException {
        //quoted can have dots, braces, spaces and esc chars in the value
        testPattern("a{java.lang.String 'b () {} \\' x.y'}");
    }

    @Test
    public void parseSingleLabelWithDoubleQuotedParamTest() throws ParseException {
        //quoted can have dots and esc chars in the value
        testPattern("a{String \"b \\\\ {} x.y\"}");
    }

    @Test
    public void parseSingleLabelWithTwoParamTest() throws ParseException {
        testPattern("a{b,c}");
    }

    @Test
    public void parseSingleLabelWithTypedParamTest() throws ParseException {
        testPattern("a{x b}");
    }

    @Test
    public void parseSingleLabelWithThreeTypedParamTest() throws ParseException {
        testPattern("a{x b,d.d1 e,f}");
    }

    @Test
    public void parseMultiLabelTest() throws ParseException {
        testPattern("a.b.c");
    }

    @Test
    public void parseMultiLabelInParenthTest() throws ParseException {
        testPattern("(a).(b).(c)");
    }

    @Test
    public void initializePathParser() throws ParseException {
        testPattern("(com.test label1{i 100}).(best label2{#,java.lang.String A}).(label3{$,X}).label4.@");
    }

    private void testPattern(String s) throws ParseException {
        pathList = JavaPathParser.parse(s);
        System.out.println(s+" -> "+pathList.stream().map(p->p.toString()).collect(Collectors.joining(".")));
    }

    StringReader r(String s) {
        StringReader sr = new StringReader(s);
        return sr;
    }

}
