package com.aegisql.java_path;

import com.aegisql.java_path.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ParserTest {

    List<TypedPathElement> pathList;

    @Before
    public void init() {
        pathList = new LinkedList<>();
    }

    @Test(expected = JavaPathRuntimeException.class)
    public void parseVoidTestShouldFail() throws ParseException {
        testPattern("");
    }

    @Test
    public void parseSingleLabelTest() throws ParseException {
        testPattern("a");
        TU.forPathElements(1,pathList)
        .acceptPathElement(pe->{
            TU.assertType(null,pe);
            TU.assertName("a",pe);
        })
        .test();
    }

    @Test
    public void parseSingleLabelInParenthTest() throws ParseException {
        testPattern("(a)");
        TU.forPathElements(1,pathList)
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("a",pe);
                })
                .test();
    }

    @Test
    public void parseSingleLabelInParenthWithTypeTest() throws ParseException {
        testPattern("(x.y a)");
        TU.forPathElements(1,pathList)
                .acceptPathElement(pe->{
                    TU.assertType("x.y",pe);
                    TU.assertName("a",pe);
                })
                .test();
    }

    @Test(expected = JavaPathRuntimeException.class)
    public void parseSingleLabelNoParenthWithTypeTestShouldFail() throws ParseException {
        testPattern("x.y a");
    }

    @Test
    public void parseSingleLabelWithParamTest() throws ParseException {
        testPattern("a(b)");
    }

    @Test
    public void parseSingleLabelWithUnicodeParamTest() throws ParseException {
        testPattern("a('ПРОВЕРКА')");
        TU.forPathElements(1,pathList)
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("a",pe);
                    TU.forTypedValues(1,pe)
                            .acceptTypedValue(tv->{
                                TU.assertType(null,tv);
                                TU.assertValueEquals("ПРОВЕРКА",tv);
                            })
                            .test();
                })
                .test();
    }

    @Test
    public void parseSingleLabelWithUnicodeAndNoQuoteParamTest() throws ParseException {
        testPattern("a(ПРОВЕРКА)");
        TU.forPathElements(1,pathList)
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("a",pe);
                    TU.forTypedValues(1,pe)
                            .acceptTypedValue(tv->{
                                TU.assertType(null,tv);
                                TU.assertValueEquals("ПРОВЕРКА",tv);
                            })
                            .test();
                })
                .test();
    }

    @Test
    public void parseSingleLabelWithSingleQuotedParamTest() throws ParseException {
        //quoted can have dots, braces, spaces and esc chars in the value
        testPattern("a(java.lang.String 'b () {} \\' x.y')");
        TU.forPathElements(1,pathList)
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("a",pe);
                    TU.forTypedValues(1,pe)
                            .acceptTypedValue(tv->{
                                TU.assertType("java.lang.String",tv);
                                TU.assertValueEquals("b () {} ' x.y",tv);
                            })
                            .test();
                })
                .test();
    }

    @Test
    public void parseSingleLabelWithDoubleQuotedParamTest() throws ParseException {
        //quoted can have dots and esc chars in the value
        testPattern("a(String \"b \\\\ {} x.y\")");
        TU.forPathElements(1,pathList)
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("a",pe);
                    TU.forTypedValues(1,pe)
                            .acceptTypedValue(tv->{
                                TU.assertType("String",tv);
                                TU.assertValueEquals("b \\ {} x.y",tv);
                            })
                            .test();
                })
                .test();
    }

    @Test
    public void parseSingleLabelWithTwoParamTest() throws ParseException {
        testPattern("a(b,c)");
        TU.forPathElements(1,pathList)
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("a",pe);
                    TU.forTypedValues(2,pe)
                            .acceptTypedValue(tv->{
                                TU.assertType(null,tv);
                                TU.assertValueEquals("b",tv);
                            })
                            .acceptTypedValue(tv->{
                                TU.assertType(null,tv);
                                TU.assertValueEquals("c",tv);
                            })
                            .test();
                })
                .test();
    }

    @Test
    public void parseSingleLabelWithTypedParamTest() throws ParseException {
        testPattern("a(x b)");
        TU.forPathElements(1,pathList)
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("a",pe);
                    TU.forTypedValues(1,pe)
                            .acceptTypedValue(tv->{
                                TU.assertType("x",tv);
                                TU.assertValueEquals("b",tv);
                            })
                            .test();
                })
                .test();
    }

    @Test
    public void parseSingleLabelWithThreeTypedParamTest() throws ParseException {
        testPattern("a(x b,d.d1 e,f)");
        TU.forPathElements(1,pathList)
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("a",pe);
                    TU.forTypedValues(3,pe)
                            .acceptTypedValue(tv->{
                                TU.assertType("x",tv);
                                TU.assertValueEquals("b",tv);
                            })
                            .acceptTypedValue(tv->{
                                TU.assertType("d.d1",tv);
                                TU.assertValueEquals("e",tv);
                            })
                            .acceptTypedValue(tv->{
                                TU.assertType(null,tv);
                                TU.assertValueEquals("f",tv);
                            })
                            .test();
                })
                .test();
    }

    @Test
    public void parseMultiLabelTest() throws ParseException {
        testPattern("a.b.c");
        TU.forPathElements(3,pathList)
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("a",pe);
                })
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("b",pe);
                })
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("c",pe);
                })
                .test();
    }

    @Test
    public void parseMultiLabelInParenthTest() throws ParseException {
        testPattern("(a).(b).(c)");
        TU.forPathElements(3,pathList)
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("a",pe);
                })
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("b",pe);
                })
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("c",pe);
                })
                .test();
    }

    @Test
    public void initializePathParser() throws ParseException {
        testPattern("(com.test label1(i 100)).(best label2(#,java.lang.String A)).(label3($,X)).label4.@");
        TU.forPathElements(5,pathList)
                .acceptPathElement(pe->{
                    TU.assertType("com.test",pe);
                    TU.assertName("label1",pe);
                    TU.forTypedValues(1,pe)
                            .acceptTypedValue(tv->{
                                TU.assertType("i",tv);
                                TU.assertValueEquals("100",tv);
                            })
                            .test();
                })
                .acceptPathElement(pe->{
                    TU.assertType("best",pe);
                    TU.assertName("label2",pe);
                    TU.forTypedValues(2,pe)
                            .acceptTypedValue(tv->{
                                TU.assertType(null,tv);
                                TU.assertValueEquals("#",tv);
                                TU.assertBackReference(0,tv);
                            })
                            .acceptTypedValue(tv->{
                                TU.assertType("java.lang.String",tv);
                                TU.assertValueEquals("A",tv);
                            })
                            .test();
                })
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("label3",pe);
                    TU.forTypedValues(2,pe)
                            .acceptTypedValue(tv->{
                                TU.assertType(null,tv);
                                TU.assertValueEquals("$",tv);
                                TU.assertValueReference(0,tv);
                            })
                            .acceptTypedValue(tv->{
                                TU.assertType(null,tv);
                                TU.assertValueEquals("X",tv);
                            })
                            .test();
                })
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("label4",pe);
                    TU.forTypedValues(0,pe)
                            .test();
                })
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("@",pe);
                    TU.forTypedValues(0,pe)
                            .test();
                })
                .test();
    }

    @Test
    public void atPathParserTest() throws ParseException {
        testPattern("a.@b(TEST).c(#2)");
        TU.forPathElements(3,pathList)
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("a",pe);
                    TU.forTypedValues(0,pe).test();
                })
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("@b",pe);
                    TU.forTypedValues(1,pe)
                            .acceptTypedValue(tv->{
                                TU.assertValueEquals("TEST",tv);
                            })
                            .test();

                })
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("c",pe);
                    TU.forTypedValues(1,pe)
                            .acceptTypedValue(tv->{
                                TU.assertValueEquals("#2",tv);
                                TU.assertBackReference(2,tv);
                            })
                            .test();
                })
                .test();
    }

    @Test(expected = JavaPathRuntimeException.class)
    public void atPathParserFailingTest() throws ParseException {
        testPattern("a.@b(TEST).c(#3)");
    }

    @Test
    public void backRefBasicTest() {
        testPattern("a.b.c(#2,STRING,#1.y.z(#2.w(W_STRING))).d.e(E_STRING)"); //#2.w(W_STRING) causes problem; 4 push 3 pop
        TU.forPathElements(5,pathList)
                .acceptPathElement(pe->{
                    TU.assertName("a",pe);
                    TU.forTypedValues(0,pe).test();
                })
                .acceptPathElement(pe->{
                    TU.assertName("b",pe);
                    TU.forTypedValues(0,pe).test();
                })
                .acceptPathElement(pe->{
                    TU.assertName("c",pe);
                    TU.forTypedValues(3,pe)
                            .acceptTypedValue(tv->{
                                TU.assertValueEquals("#2",tv);
                                TU.assertBackReference(2,tv);
                            })
                            .acceptTypedValue(tv->{
                                TU.assertValueEquals("STRING",tv);
                            })
                            .acceptTypedValue(tv->{
                                TU.assertValueEquals("#1",tv);
                                TU.assertBackReference(1,tv);
                                TU.forPathElements(2,tv)
                                        .acceptPathElement(pe2->{
                                            TU.assertName("y",pe2);
                                            TU.forTypedValues(0,pe2).test();
                                        })
                                        .acceptPathElement(pe2->{
                                            TU.assertName("z",pe2);
                                            TU.forTypedValues(1,pe2)
                                                    .acceptTypedValue(tv2->{
                                                        TU.assertValueEquals("#2",tv2);
                                                        TU.assertBackReference(2,tv2);
                                                        TU.forPathElements(1,tv2)
                                                                .acceptPathElement(pe3->{
                                                                    TU.assertName("w",pe3);
                                                                    TU.forTypedValues(1,pe3)
                                                                            .acceptTypedValue(tv3->{
                                                                                TU.assertValueEquals("W_STRING",tv3);
                                                                            })
                                                                            .test();
                                                                })
                                                                .test();
                                                    })
                                                    .test();
                                        })
                                        .test();
                            })
                            .test();
                })
                .acceptPathElement(pe->{
                    TU.assertName("d",pe);
                    TU.forTypedValues(0,pe).test();
                })
                .acceptPathElement(pe->{
                    TU.assertName("e",pe);
                    TU.forTypedValues(1,pe)
                            .acceptTypedValue(tv->{
                                TU.assertValueEquals("E_STRING",tv);
                            })
                            .test();
                })
                .test();
    }

    @Test
    public void backRefTest2() {
        testPattern("a.setDesc(#0.id,#0.desc.setDesc(fromTest).val).name");
        TU.forPathElements(3,pathList)
                .acceptPathElement(pe->{
                    TU.assertName("a",pe);
                    TU.forTypedValues(0,pe).test();
                })
                .acceptPathElement(pe->{
                    TU.assertName("setDesc",pe);
                    TU.forTypedValues(2,pe)
                            .acceptTypedValue(tv->{
                                TU.assertValueEquals("#0",tv);
                                TU.forPathElements(1,tv)
                                        .acceptPathElement(pe2->{
                                            TU.assertName("id",pe2);
                                        })
                                        .test();
                            })
                            .acceptTypedValue(tv->{
                                TU.assertValueEquals("#0",tv);
                                TU.forPathElements(3,tv)
                                        .acceptPathElement(pe2->{
                                            TU.assertName("desc",pe2);
                                        })
                                        .acceptPathElement(pe2->{
                                            TU.assertName("setDesc",pe2);
                                            TU.forTypedValues(1,pe2)
                                                    .acceptTypedValue(tv2->{
                                                        TU.assertValueEquals("fromTest",tv2);
                                                    })
                                                    .test();
                                        })
                                        .acceptPathElement(pe2->{
                                            TU.assertName("val",pe2);
                                        })
                                        .test();
                            })
                            .test();
                })
                .acceptPathElement(pe->{
                    TU.assertName("name",pe);
                    TU.forTypedValues(0,pe).test();
                })
                .test();
    }

    @Test
    public void backRefTest3() {
        testPattern( "("+StringBuilder.class.getName()+" #).append($0.substring(int 1)).append($1).append($2).append($3.substring(int 1,int 3))");
        TU.forPathElements(5,pathList)
                .acceptPathElement(pe->{
                    TU.assertName("#",pe);
                    TU.assertType(StringBuilder.class.getName(),pe);
                })
                .acceptPathElement(pe->{
                    TU.assertName("append",pe);
                    TU.forTypedValues(1,pe)
                            .acceptTypedValue(tv->{
                                TU.assertValueEquals("$0",tv);
                                TU.assertValueReference(0,tv);
                                TU.forPathElements(1,tv)
                                        .acceptPathElement(pe2->{
                                            TU.forTypedValues(1,pe2)
                                                    .acceptTypedValue(tv2->{
                                                        TU.assertType("int",tv2);
                                                        TU.assertValueEquals("1",tv2);
                                                    })
                                                    .test();
                                        })
                                        .test();
                            })
                            .test();
                })
                .acceptPathElement(pe->{
                    TU.assertName("append",pe);
                    TU.forTypedValues(1,pe)
                            .acceptTypedValue(tv->{
                                TU.assertValueEquals("$1",tv);
                                TU.assertValueReference(1,tv);
                            })
                            .test();
                })
                .acceptPathElement(pe->{
                    TU.assertName("append",pe);
                    TU.forTypedValues(1,pe)
                            .acceptTypedValue(tv->{
                                TU.assertValueEquals("$2",tv);
                                TU.assertValueReference(2,tv);
                            })
                            .test();
                })
                .acceptPathElement(pe->{
                    TU.assertName("append",pe);
                    TU.forTypedValues(1,pe)
                            .acceptTypedValue(tv->{
                                TU.assertValueEquals("$3",tv);
                                TU.assertValueReference(3,tv);
                                TU.forPathElements(1,tv)
                                        .acceptPathElement(pe2->{
                                            TU.forTypedValues(2,pe2)
                                                    .acceptTypedValue(tv2->{
                                                        TU.assertType("int",tv2);
                                                        TU.assertValueEquals("1",tv2);
                                                    })
                                                    .acceptTypedValue(tv2->{
                                                        TU.assertType("int",tv2);
                                                        TU.assertValueEquals("3",tv2);
                                                    })
                                                    .test();
                                        })
                                        .test();
                            })
                            .test();
                })
                .test();
    }



    @Test
    public void emptyStringTest() {
        testPattern("a('')");
        TU.forPathElements(1,pathList)
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("a",pe);
                    TU.forTypedValues(1,pe)
                            .acceptTypedValue(tv->{
                                TU.assertType(null,tv);
                                TU.assertValueEquals("",tv);
                            })
                            .test();
                })
                .test();
    }

    @Test
    public void noParameterTest() {
        testPattern("a()");
        TU.forPathElements(1,pathList)
                .acceptPathElement(pe->{
                    TU.assertType(null,pe);
                    TU.assertName("a",pe);
                    TU.forTypedValues(0,pe).test();
                })
                .test();
    }

    private void testPattern(String s) {
        pathList = JavaPathParser.parse(s);
        System.out.println(s+" -> "+pathList.stream().map(p->p.toString()).collect(Collectors.joining(".")));
    }

}
