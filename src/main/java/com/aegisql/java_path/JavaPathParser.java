package com.aegisql.java_path;

import com.aegisql.java_path.parser.*;

import java.io.StringReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class JavaPathParser {

    private static class Visitor implements CCJavaPathParserVisitor {

        private String toString(Object o) {
            return o == null ? null : o.toString();
        }

        @Override
        public Object visit(SimpleNode node, Object data) {
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTfullPath node, Object data) {
            LinkedList<TypedPathElement> pathList = (LinkedList<TypedPathElement>) data;
            TypedPathElement typedPathElement = new TypedPathElement();
            pathList.add(typedPathElement);
            typedPathElement.setType(toString(node.jjtGetValue()));
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTtypedPathElement node, Object data) {
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTpathElement node, Object data) {
            LinkedList<TypedPathElement> pathList = (LinkedList<TypedPathElement>) data;
            pathList.getLast().setName(toString(node.jjtGetValue()));
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTfullType node, Object data) {
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTtype node, Object data) {
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTparameters node, Object data) {
            LinkedList<TypedPathElement> pathList = (LinkedList<TypedPathElement>) data;
            pathList.getLast().addParameter((TypedValue) node.jjtGetValue());
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTparameter node, Object data) {
            return node.childrenAccept(this,data);
        }
    }


    public static List<TypedPathElement> parse(String path) {
        LinkedList<TypedPathElement> elements = new LinkedList<>();
        CCJavaPathParser parser = new CCJavaPathParser(r(path));
        SimpleNode sn = null;
        try {
            sn = parser.fullPath();
            sn.jjtAccept(new Visitor(),elements);
        } catch (ParseException e) {
            throw new JavaPathRuntimeException("Failed parsing JavaPath '"+path+"'",e);
        }
        return Collections.unmodifiableList(elements);
    }

    private static StringReader r(String s) {
        StringReader sr = new StringReader(s);
        return sr;
    }

}
