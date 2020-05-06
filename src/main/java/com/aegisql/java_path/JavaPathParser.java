package com.aegisql.java_path;

import com.aegisql.java_path.parser.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The type Java path parser.
 */
public class JavaPathParser {

    private final static Logger LOG = LoggerFactory.getLogger(JavaPathParser.class);

    private static class Visitor implements CCJavaPathParserVisitor {

        private LinkedList<LinkedList<TypedPathElement>> stack = new LinkedList<>();
        private LinkedList<TypedPathElement> rootPath;
        private LinkedList<TypedPathElement> currentPath;
        private int maxBackRef = 0;
        private boolean option;

        /**
         * Instantiates a new Visitor.
         *
         * @param rootPath the root path
         */
        public Visitor(LinkedList<TypedPathElement> rootPath) {
            this.rootPath = rootPath;
            this.currentPath = rootPath;
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
            if(stack.size() > 0) {
                currentPath = stack.pop();
            }
            LOG.trace("parse:, current: {} stack: {}",currentPath, stack);
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTlParenthesis node, Object data) {
            if(stack.size() > 0) {
                stack.push(currentPath);
            }
            LOG.trace("parse:( current: {} stack: {}",currentPath, stack);
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTrParenthesis node, Object data) {
            if(stack.size() > 0) {
                currentPath = stack.pop();
            }
            LOG.trace("parse:) current: {} stack: {}",currentPath, stack);
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASToption node, Object data) {
            option = (boolean) node.jjtGetValue();
            LOG.trace("parse:? current: {} stack: {}",currentPath, stack);
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTparse node, Object data) {
            TypedPathElement typedPathElement = (TypedPathElement) node.jjtGetValue();
            if(typedPathElement != null) {
                currentPath.add(typedPathElement);
            }
            LOG.trace("parse:parse {}, {} stack: {}",node.jjtGetValue(),typedPathElement,stack);
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTfullPath node, Object data) {
            currentPath = rootPath;
            stack.clear();
            TypedPathElement typedPathElement = new TypedPathElement();
            String fullType = toString(node.jjtGetValue());
            String type = null;
            String factory = null;
            if(fullType != null) {
                String[] pair = fullType.split("::",2);
                type = pair[0];
                if(pair.length == 2) {
                    factory = pair[1];
                }
            }
            typedPathElement.setType(type);
            typedPathElement.setFactory(factory);
            if(option) {
                currentPath.getLast().setOptionalPathElement(typedPathElement);
            } else {
                currentPath.add(typedPathElement);
            }
            LOG.trace("parse:fullPath type: '{}' {} current: {}",fullType,typedPathElement, currentPath);
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTpathElement node, Object data) {
            TypedPathElement typedPathElement = null;
            if(stack.size() > 0) {
                typedPathElement = new TypedPathElement();
                TypedPathElement last;
                if(option) {
                    last = rootPath.getLast().getOptionalPathElement();
                } else {
                    last = rootPath.getLast();
                }
                typedPathElement.setType(last.getParameters().getLast().getType());
                currentPath.add(typedPathElement);
            } else {
                if (option) {
                    typedPathElement = currentPath.getLast().getOptionalPathElement();
                } else {
                    typedPathElement = currentPath.getLast();
                }
            }
            typedPathElement.setName(toString(node.jjtGetValue()));
            maxBackRef++;
            LOG.trace("parse:pathElement '{}' maxBackRef {} current: {} stack: {}",node.jjtGetValue(), maxBackRef, currentPath, stack);
            return node.childrenAccept(this,data);
        }

        @Override
        public Object visit(ASTparameters node, Object data) {
            TypedValue typedValue = (TypedValue) node.jjtGetValue();
            TypedPathElement typedPathElement;
            if(option) {
                typedPathElement = currentPath.getLast().getOptionalPathElement();
            } else {
                typedPathElement = currentPath.getLast();
            }
            typedPathElement.addParameter(typedValue);
            if(typedValue.isHashSign()) {
                if(typedValue.getBackRefIdx() >= maxBackRef) {
                    throw new JavaPathRuntimeException("Back reference #"+typedValue.getBackRefIdx()+" is not visible at position "+maxBackRef);
                }
                stack.push(currentPath);
                currentPath = typedValue.getTypedPathElements();
            } else if(typedValue.isDollarSign()) {
                stack.push(currentPath);
                currentPath = typedValue.getTypedPathElements();
            }
            LOG.trace("parse:parameters '{}' current: {} stack: {}",typedValue,currentPath,stack);
            return node.childrenAccept(this,data);
        }

    }


    /**
     * Parse list.
     *
     * @param path the path
     * @return the list
     */
    public static List<TypedPathElement> parse(String path) {
        LOG.debug("parsing {}",path);
        LinkedList<TypedPathElement> elements = new LinkedList<>();
        CCJavaPathParser parser = new CCJavaPathParser(r(path));
        SimpleNode sn = null;
        try {
            sn = parser.parse();
            sn.jjtAccept(new Visitor(elements),elements);
        } catch (ParseException e) {
            throw new JavaPathRuntimeException("Failed parsing JavaPath '"+path+"'",e);
        }
        LOG.debug("parsing of {} complete: {}",path,elements);
        return Collections.unmodifiableList(elements);
    }

    private static StringReader r(String s) {
        StringReader sr = new StringReader(s);
        return sr;
    }

}
