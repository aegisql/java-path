package com.aegisql.java_path;

import com.aegisql.java_path.parser.*;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.LinkedList;

public class ParserTest {

    LinkedList<TypedPathElement> pathList;

    @Before
    public void init() {
        pathList = new LinkedList<>();
    }

    static class Visitor implements CCJavaPathParserVisitor {
        @Override
        public Object visit(SimpleNode node, Object data) {
            LinkedList<TypedPathElement> pathList = (LinkedList<TypedPathElement>) data;
            System.out.println(node+" "+node.jjtGetValue());
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
            System.out.println(node+" "+node.jjtGetValue());
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTtypedPathElement node, Object data) {
            LinkedList<TypedPathElement> pathList = (LinkedList<TypedPathElement>) data;
            System.out.println(node+" "+node.jjtGetValue());
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTpathElement node, Object data) {
            LinkedList<TypedPathElement> pathList = (LinkedList<TypedPathElement>) data;
            pathList.getLast().setName(node.jjtGetValue().toString());
            System.out.println(node+" "+node.jjtGetValue());
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTfullType node, Object data) {
            LinkedList<TypedPathElement> pathList = (LinkedList<TypedPathElement>) data;
            System.err.println("Parent of fullType "+node.jjtGetParent().getClass());
            System.out.println(node+" "+node.jjtGetValue());
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTtype node, Object data) {
            LinkedList<TypedPathElement> pathList = (LinkedList<TypedPathElement>) data;
            System.out.println(node+" "+node.jjtGetValue());
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTparameters node, Object data) {
            LinkedList<TypedPathElement> pathList = (LinkedList<TypedPathElement>) data;
            pathList.getLast().addParameter((TypedValue) node.jjtGetValue());
            System.out.println(node+" "+node.jjtGetValue());
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTparameter node, Object data) {
            LinkedList<TypedPathElement> pathList = (LinkedList<TypedPathElement>) data;
            System.out.println(node+" "+node.jjtGetValue());
            return node.childrenAccept(this,data);
        }
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

    @Test
    public void parseSingleLabelWithParamTest() throws ParseException {
        testPattern("a{b}");
    }

    @Test
    public void parseSingleLabelWithSingleQuotedParamTest() throws ParseException {
        //quoted can have dots and esc chars in the value
        testPattern("a{String 'b \\\\{\\\\} x.y'}");
    }

    @Test
    public void parseSingleLabelWithDoubleQuotedParamTest() throws ParseException {
        //quoted can have dots and esc chars in the value
        testPattern("a{String \"b \\\\{\\\\} x.y\"}");
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
        testPattern("a.(b).(c)");
    }

    @Test
    public void initializePathParser() throws ParseException {
        testPattern("(com.test label1{i 100}).(best label2{java.lang.String A}).(label3{X}).label4.@");
    }

    private void testPattern(String s) throws ParseException {
        CCJavaPathParser parser = new CCJavaPathParser(r(s));
        SimpleNode sn = parser.fullPath();
        sn.jjtAccept(new Visitor(),pathList);
        System.out.println(s+" -> "+pathList);
    }

    StringReader r(String s) {
        StringReader sr = new StringReader(s);
        return sr;
    }

}
