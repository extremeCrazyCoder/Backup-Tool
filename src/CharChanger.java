import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CharChanger {
    private static String supportedCharsV1 = " 0123456789abcdefghijklmnopqrstuvwxyz"
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ,;.:-_#'+*!\u00A7$%&?\u00df/\\()=<>\u00e4\u00f6"
            + "\u00fc\u00c4\u00d6\u00dc\"\n\r\t\u007E\u0040{}";
    
    public static String decodeV1(String toDecode) {
        List<Replacement> replaceOptions = new ArrayList<Replacement>();
        replaceOptions.add(new Replacement("y0y", "y"));
        replaceOptions.add(new Replacement("y0T", "\t"));
        replaceOptions.add(new Replacement("y0R", "\r"));
        replaceOptions.add(new Replacement("y0N", "\n"));
        replaceOptions.add(new Replacement("y00", "$"));
        replaceOptions.add(new Replacement("y01", "#"));
        replaceOptions.add(new Replacement("y02", "'"));
        replaceOptions.add(new Replacement("y03", "+"));
        replaceOptions.add(new Replacement("y04", "*"));
        replaceOptions.add(new Replacement("y05", "!"));
        replaceOptions.add(new Replacement("y06", "\u00A7"));
        replaceOptions.add(new Replacement("y08", "%"));
        replaceOptions.add(new Replacement("y09", "&"));
        replaceOptions.add(new Replacement("y0a", "/"));
        replaceOptions.add(new Replacement("y0b", "\\"));
        replaceOptions.add(new Replacement("y0c", "("));
        replaceOptions.add(new Replacement("y0d", ")"));
        replaceOptions.add(new Replacement("y0e", "="));
        replaceOptions.add(new Replacement("y0f", "<"));
        replaceOptions.add(new Replacement("y0g", ">"));
        replaceOptions.add(new Replacement("y0h", "\u00e4"));
        replaceOptions.add(new Replacement("y0i", "\u00f6"));
        replaceOptions.add(new Replacement("y0j", "\u00fc"));
        replaceOptions.add(new Replacement("y0k", "\u00c4"));
        replaceOptions.add(new Replacement("y0l", "\u00d6"));
        replaceOptions.add(new Replacement("y0m", "\u00dc"));
        replaceOptions.add(new Replacement("y0n", "\""));
        replaceOptions.add(new Replacement("y07", "?"));
        replaceOptions.add(new Replacement("y0o", "\u00df"));
        replaceOptions.add(new Replacement("y0p", "-"));
        replaceOptions.add(new Replacement("y0q", "_"));
        replaceOptions.add(new Replacement("y0r", ":"));
        replaceOptions.add(new Replacement("y0s", "."));
        replaceOptions.add(new Replacement("y0t", ","));
        replaceOptions.add(new Replacement("y0u", ";"));
        replaceOptions.add(new Replacement("y0v", "\u007E"));
        replaceOptions.add(new Replacement("y0w", "\u0040"));
        replaceOptions.add(new Replacement("y0x", "{"));
        replaceOptions.add(new Replacement("y0z", "}"));
        
        toDecode = replaceAll(toDecode, replaceOptions);
        return toDecode;
    }
    
    public static String codeV1(String toCode) throws IOException {
        //Prepare text
        checkCharsV1(toCode);
        List<Replacement> replaceOptions = new ArrayList<Replacement>();
        replaceOptions.add(new Replacement("y", "y0y"));
        replaceOptions.add(new Replacement("\t", "y0T"));
        replaceOptions.add(new Replacement("\r", "y0R"));
        replaceOptions.add(new Replacement("\n", "y0N"));
        replaceOptions.add(new Replacement("$", "y00"));
        replaceOptions.add(new Replacement("#", "y01"));
        replaceOptions.add(new Replacement("'", "y02"));
        replaceOptions.add(new Replacement("+", "y03"));
        replaceOptions.add(new Replacement("*", "y04"));
        replaceOptions.add(new Replacement("!", "y05"));
        replaceOptions.add(new Replacement("\u00A7", "y06"));
        replaceOptions.add(new Replacement("%", "y08"));
        replaceOptions.add(new Replacement("&", "y09"));
        replaceOptions.add(new Replacement("/", "y0a"));
        replaceOptions.add(new Replacement("\\", "y0b"));
        replaceOptions.add(new Replacement("(", "y0c"));
        replaceOptions.add(new Replacement(")", "y0d"));
        replaceOptions.add(new Replacement("=", "y0e"));
        replaceOptions.add(new Replacement("<", "y0f"));
        replaceOptions.add(new Replacement(">", "y0g"));
        replaceOptions.add(new Replacement("\u00e4", "y0h"));
        replaceOptions.add(new Replacement("\u00f6", "y0i"));
        replaceOptions.add(new Replacement("\u00fc", "y0j"));
        replaceOptions.add(new Replacement("\u00c4", "y0k"));
        replaceOptions.add(new Replacement("\u00d6", "y0l"));
        replaceOptions.add(new Replacement("\u00dc", "y0m"));
        replaceOptions.add(new Replacement("\"", "y0n"));
        replaceOptions.add(new Replacement("?", "y07"));
        replaceOptions.add(new Replacement("\u00df", "y0o"));
        replaceOptions.add(new Replacement("-", "y0p"));
        replaceOptions.add(new Replacement("_", "y0q"));
        replaceOptions.add(new Replacement(":", "y0r"));
        replaceOptions.add(new Replacement(".", "y0s"));
        replaceOptions.add(new Replacement(",", "y0t"));
        replaceOptions.add(new Replacement(";", "y0u"));
        replaceOptions.add(new Replacement("\u007E", "y0v"));
        replaceOptions.add(new Replacement("\0040", "y0w"));
        replaceOptions.add(new Replacement("{", "y0x"));
        replaceOptions.add(new Replacement("}", "y0z"));
        
        toCode = replaceAll(toCode, replaceOptions);
        
        return toCode;
    }
    
    private static String replaceAll(String unreplaced, List<Replacement> replaceOptions) {
        String replaced = "";
        
        while(unreplaced.length() > 0) {
            boolean charReplaced = false;
            
            for(Replacement option: replaceOptions) {
                if(unreplaced.startsWith(option.getToReplace())) {
                    replaced+= option.getReplacement();
                    unreplaced = unreplaced.substring(option.getToReplace().length());
                    charReplaced = true;
                    break;
                }
            }
            
            if(!charReplaced) {
                replaced+= unreplaced.charAt(0);
                unreplaced = unreplaced.substring(1);
            }
        }
        
        return replaced;
    }
    
    private static void checkCharsV1(String toCheck) throws IOException {
        for(int i = 0; i < toCheck.length(); i++)
            if(!supportedCharsV1.contains("" + toCheck.charAt(i)))
                throw new IOException("Wrong Char: " + toCheck.charAt(i) + "\n" + toCheck);
    }
    
    public static String decodeNormal(String todo) {
        return decodeV1(todo);
    }
    
    public static String codeNormal(String todo) throws IOException {
        return codeV1(todo);
    }

    public static int getCurrentVerion() {
        return 1;
    }

    public static String decode(String toDecode, int charChangerVersion) {
        if(charChangerVersion == 1)
            return decodeV1(toDecode);
        return null;
    }
}

class Replacement {
    String toReplace;
    String replacement;
    
    public Replacement(String toReplace, String replacement) {
        this.toReplace = toReplace;
        this.replacement = replacement;
    }
    
    String getToReplace() {
        return toReplace;
    }
    
    String getReplacement() {
        return replacement;
    }
}