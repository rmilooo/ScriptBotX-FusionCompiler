package org.Fusion.Compiler;

import org.Fusion.Lexer.Token;

import java.util.List;

public class Compiler {

    public String compile(List<Token> tokens) {
        StringBuilder pythonCode = new StringBuilder();

        for (Token token : tokens) {
            switch (token.getType()) {
                case BOT:
                    pythonCode.append("bot = Bot(\"" + token.getValue() + "\")\n");
                    break;
                case TOKEN:
                    pythonCode.append("token = Token(\"" + token.getValue() + "\")\n");
                    break;
                case ON_READY:
                    pythonCode.append("def on_ready():\n");
                    pythonCode.append("    pass\n");
                    break;
                case COMMAND:
                    pythonCode.append("def command_" + token.getValue() + "():\n");
                    pythonCode.append("    pass\n");
                    break;
                case REPLY:
                    pythonCode.append("def reply(message):\n");
                    pythonCode.append("    print(\"" + token.getValue() + "\")\n");
                    break;
                case LOG:
                    pythonCode.append("log(\"" + token.getValue() + "\")\n");
                    break;
                case UNKNOWN:
                    pythonCode.append("# Unknown token: " + token.getValue() + "\n");
                    break;
                case EOF:
                    pythonCode.append("# End of file\n");
                    break;
                default:
                    break;
            }
        }

        return pythonCode.toString();
    }

}
