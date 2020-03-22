package com.aegisql.java_path;

import com.aegisql.java_path.parser.*;
import org.junit.Test;

import java.io.StringReader;

public class ParserTest {

    @Test
    public void parseSingleLabelTest() throws ParseException {
        CCJavaPathParser parser = new CCJavaPathParser(r("a"));
        SimpleNode sn = parser.fullLabel();
    }

    @Test
    public void parseSingleLabelInParenthTest() throws ParseException {
        CCJavaPathParser parser = new CCJavaPathParser(r("(a)"));
        SimpleNode sn = parser.fullLabel();
    }

    @Test
    public void parseSingleLabelInParenthWithTypeTest() throws ParseException {
        CCJavaPathParser parser = new CCJavaPathParser(r("(x a)"));
        SimpleNode sn = parser.fullLabel();
    }

    @Test
    public void parseSingleLabelWithParamTest() throws ParseException {
        CCJavaPathParser parser = new CCJavaPathParser(r("a{b}"));
        SimpleNode sn = parser.fullLabel();
    }


    @Test
    public void parseSingleLabelWithSingleQuotedParamTest() throws ParseException {
        CCJavaPathParser parser = new CCJavaPathParser(r("a{'b x'}"));
        SimpleNode sn = parser.fullLabel();
    }

    @Test
    public void parseSingleLabelWithTwoParamTest() throws ParseException {
        CCJavaPathParser parser = new CCJavaPathParser(r("a{b,c}"));
        SimpleNode sn = parser.fullLabel();
    }

    @Test
    public void parseSingleLabelWithTypedParamTest() throws ParseException {
        CCJavaPathParser parser = new CCJavaPathParser(r("a{x b}"));
        SimpleNode sn = parser.fullLabel();
    }

    @Test
    public void parseSingleLabelWithThreeTypedParamTest() throws ParseException {
        CCJavaPathParser parser = new CCJavaPathParser(r("a{x b,d.d1 e,f}"));
        SimpleNode sn = parser.fullLabel();
    }

    @Test
    public void parseMultiLabelTest() throws ParseException {
        CCJavaPathParser parser = new CCJavaPathParser(r("a.b.c"));
        SimpleNode sn = parser.fullLabel();
    }

    @Test
    public void parseMultiLabelInParenthTest() throws ParseException {
        CCJavaPathParser parser = new CCJavaPathParser(r("a.(b).(c)"));
        SimpleNode sn = parser.fullLabel();
    }

    @Test
    public void initializePathParser() throws ParseException {
        CCJavaPathParser parser = new CCJavaPathParser(r(
"(com.test label1{i 100}).(best label2{java.lang.String A}).(label3{X}).label4.@"));
        SimpleNode sn = parser.fullLabel();
        System.out.println(sn);
        sn.jjtAccept(new CCJavaPathParserVisitor() {
            @Override
            public Object visit(SimpleNode node, Object data) {
                return null;
            }

            @Override
            public Object visit(ASTfullLabel node, Object data) {
                return null;
            }

            @Override
            public Object visit(ASTtypedLabel node, Object data) {
                return null;
            }

            @Override
            public Object visit(ASTlabel node, Object data) {
                return null;
            }

            @Override
            public Object visit(ASTfullType node, Object data) {
                return null;
            }

            @Override
            public Object visit(ASTtype node, Object data) {
                return null;
            }

            @Override
            public Object visit(ASTparameters node, Object data) {
                return null;
            }

            @Override
            public Object visit(ASTparameter node, Object data) {
                return null;
            }
        },"TEST");
    }

    StringReader r(String s) {
        StringReader sr = new StringReader(s);
        return sr;
    }

}
