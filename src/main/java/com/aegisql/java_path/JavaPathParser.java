package com.aegisql.java_path;

import com.aegisql.java_path.parser.*;

import java.io.StringReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class JavaPathParser {

    private static class Visitor implements CCJavaPathParserVisitor {

        private LinkedList<TypedPathElement> rootPath;
        private LinkedList<LinkedList<TypedPathElement>> stack = new LinkedList<>();
        private int maxBackRef = 0;

        public Visitor(LinkedList<TypedPathElement> rootPath) {
            this.rootPath = rootPath;
            stack.push(rootPath);
        }

        private String toString(Object o) {
            return o == null ? null : o.toString();
        }

        @Override
        public Object visit(SimpleNode node, Object data) {
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTcomma node, Object data) {
            if(stack.size() > 1) {
                stack.pop();
            }
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTrParenthesis node, Object data) {
            if(stack.size() > 1) {
                stack.pop();
            }
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTparse node, Object data) {
            TypedPathElement typedPathElement = (TypedPathElement) node.jjtGetValue();
            if(typedPathElement != null) {
                stack.getFirst().add(typedPathElement);
            }
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTfullPath node, Object data) {
            TypedPathElement typedPathElement = new TypedPathElement();
            stack.getFirst().add(typedPathElement);
            typedPathElement.setType(toString(node.jjtGetValue()));
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTpathElement node, Object data) {
            if(stack.size() > 1) {
                TypedPathElement typedPathElement = new TypedPathElement();
                stack.getFirst().add(typedPathElement);
            }
            stack.getFirst().getLast().setName(toString(node.jjtGetValue()));
            maxBackRef++;
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTparameters node, Object data) {
            TypedValue typedValue = (TypedValue) node.jjtGetValue();
            stack.getFirst().getLast().addParameter(typedValue);
            String ref = typedValue.getValue();
            if(ref.startsWith("#")) {
                if(ref.length() > 1) {
                    int refPos = Integer.valueOf(ref.substring(1));
                    if(refPos >= maxBackRef) {
                        throw new JavaPathRuntimeException("Back reference "+ref+" is not visible at position "+maxBackRef);
                    }
                }
                stack.push(typedValue.getTypedPathElements());
            }
            return node.childrenAccept(this,data);
        }

    }


    public static List<TypedPathElement> parse(String path) {
        LinkedList<TypedPathElement> elements = new LinkedList<>();
        CCJavaPathParser parser = new CCJavaPathParser(r(path));
        SimpleNode sn = null;
        try {
            sn = parser.parse();
            sn.jjtAccept(new Visitor(elements),elements);
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
