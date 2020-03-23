package com.aegisql.java_path;

public class TypedValue {
    private String value;
    private String type;

    public String getValue() {
        return value;
    }

    private String unEscape(String s) {
        if(s == null) return null;
        char[] chars = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        boolean escFound = false;
        for(int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if(i == 0 || i == chars.length -1){
                if(ch == '\'') continue;
                if(ch == '"') continue;
            }
            if(ch == '\\' && ! escFound) {
                escFound = true;
                continue;
            }
            sb.append(ch);
            escFound = false;
        }
        return sb.toString();
    }

    public void setValue(String value) {
        this.value = unEscape(value);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("");
        if(type != null) {
            sb.append(type).append(" ");
        }
        sb.append(value);
        return sb.toString();
    }
}
