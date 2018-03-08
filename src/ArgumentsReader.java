import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


public class ArgumentsReader {
    private List<Argument> arguments = new ArrayList<Argument>();
    
    public ArgumentsReader(String[] args) throws IOException {
        for(int i = 0; i < args.length; i++) {
            if(!args[i].startsWith("-")) {
                throw new IOException("Missing \"-\" at Argument " + i);
            }
            else if(args.length == i + 1 || args[i + 1].startsWith("-")) {
                arguments.add(new Argument(args[i].substring(1), null));
            }
            else {
                arguments.add(new Argument(args[i].substring(1), args[i + 1]));
                i++;
            }
        }
    }
    
    Argument getIndex(String index) {
        for(int i = 0; i < arguments.size(); i++)
            if(arguments.get(i).getIndex().startsWith(index))
                return arguments.get(i);
        
        return null;
    }
    
    boolean indexExists(String index) {
        for(int i = 0; i < arguments.size(); i++)
            if(arguments.get(i).getIndex().startsWith(index))
                return true;
        
        return false;
    }
}

class Argument {
    private String index, value;
    
    Argument(String index, String value) {
        this.index = index;
        this.value = value;
    }
    
    String getIndex() {
        return index;
    }
    
    String getValue() {
        return value;
    }
}