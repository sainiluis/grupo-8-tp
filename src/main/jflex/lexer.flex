package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.ParserSym;
import lyc.compiler.model.*;
import static lyc.compiler.constants.Constants.*;

%%

%public
%class Lexer
%unicode
%cup
%line
%column
%throws CompilerException
%eofval{
  return symbol(ParserSym.EOF);
%eofval}

%{
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Identation =  [ \t\f]

Plus = "+"
Mult = "*"
Sub = "-"
Div = "/"
Assig = ":="
If = "si"
Else = "sino"
OpenBracket = "("
CloseBracket = ")"
Letter = [a-zA-Z]
Digit = [0-9]
GreaterThan = ">"
LessThan = "<"
GreaterEqual = ">="
LessEqual = "<="
Equals = "="
NotEquals = "!="
LlaveA = "{"
LlaveC = "}"
DosPuntos = ":"
Coma = ","
Float = "Float"
Int = "Int"
String = "String"
Init = "init"
While = "mientras"
And = "AND"
Or = "OR"
Not = "NOT"
Write = "escribir"
Read = "leer"
UntilLoop = "UntilLoop"
AplicarDesc = "aplicarDescuento"
CorcheteA = "["
CorcheteC = "]"


WhiteSpace = {LineTerminator} | {Identation}
Identifier = {Letter} ({Letter}|{Digit})*
IntegerConstant = {Digit}+
StringConstant =  \"([^\"]|\\\")*\"
FloatConstant = {IntegerConstant} "." {IntegerConstant} | "." {IntegerConstant} | {IntegerConstant} "."
Comment = "*-" ~ "-*"

%%

<YYINITIAL> {



   /* Palabras Reservadas */
  {If}                                      { return symbol(ParserSym.IF); }
  {While}                                   { return symbol(ParserSym.WHILE); }
  {Float}                                   { return symbol(ParserSym.FLOAT); }
  {Int}                                     { return symbol(ParserSym.INT); }
  {String}                                  { return symbol(ParserSym.STRING); }
  {Init}                                    { return symbol(ParserSym.INIT); }
  {Or}                                      { return symbol(ParserSym.OR); }
  {And}                                     { return symbol(ParserSym.AND); }
  {Not}                                     { return symbol(ParserSym.NOT); }
  {Write}                                   { return symbol(ParserSym.WRITE); }
  {Read}                                    { return symbol(ParserSym.READ); }
  {UntilLoop}                               { return symbol(ParserSym.UNTIL_LOOP); }
  {AplicarDesc}                             { return symbol(ParserSym.APLICAR_DESC); }
  {Else}                                    { return symbol(ParserSym.ELSE); }


  /* operators */
  {Plus}                                    { return symbol(ParserSym.PLUS); }
  {Sub}                                     { return symbol(ParserSym.SUB); }
  {Mult}                                    { return symbol(ParserSym.MULT); }
  {Div}                                     { return symbol(ParserSym.DIV); }
  {Assig}                                   { return symbol(ParserSym.ASSIG);}
  {OpenBracket}                             { return symbol(ParserSym.OPEN_BRACKET); }
  {CloseBracket}                            { return symbol(ParserSym.CLOSE_BRACKET); }
  {DosPuntos}                               { return symbol(ParserSym.DOSPUNTOS); }
  {Coma}                                    { return symbol(ParserSym.COMA); }
  
  {GreaterThan}                             { return symbol(ParserSym.GREATERTHAN); }
  {LessThan}                                { return symbol(ParserSym.LESSTHAN); }
  {GreaterEqual}                            { return symbol(ParserSym.GREATEREQUAL); }
  {LessEqual}                               { return symbol(ParserSym.LESSEQUAL); }
  {Equals}                                  { return symbol(ParserSym.EQUALS); }
  {NotEquals}                               { return symbol(ParserSym.NOTEQUALS); }
  {LlaveA}                                  { return symbol(ParserSym.LLAVEA); }
  {LlaveC}                                  { return symbol(ParserSym.LLAVEC); }
  {CorcheteA}                               { return symbol(ParserSym.CORCHETE_A); }
  {CorcheteC}                               { return symbol(ParserSym.CORCHETE_C); }

  // Comment
  {Comment}                          { /* ignore */ }

  /* Identifiers */
  {Identifier}                             { return symbol(ParserSym.IDENTIFIER, yytext()); }

    /* Constants */
  {FloatConstant}                         {

    // Verificamos si el float está dentro del rango
    if (Float.parseFloat(yytext()) >= MIN_FLOAT_32 && Float.parseFloat(yytext()) <= MAX_FLOAT_32) {
        return symbol(ParserSym.FLOAT_CONSTANT, yytext());
    } else {
        throw new NumberFormatException("Float out of range");
    }
  }

  {StringConstant}                         { 
    
    
    String textoSinComillas = yytext().substring(1, yytext().length() - 1);

    // Verificamos que el string no supere los 40 caracteres
    if (textoSinComillas.length() <= MAX_LENGTH_STRING) {
        return symbol(ParserSym.STRING_CONSTANT, yytext()); 
    } else {
        throw new InvalidLengthException("Invalid string length");
    }

  }


  {IntegerConstant}       
  {

    // Verificamos si el int está dentro del rango
    if (Integer.parseInt(yytext()) >= MIN_INT_16 && Integer.parseInt(yytext()) <= MAX_INT_16) {
        return symbol(ParserSym.INTEGER_CONSTANT, yytext());
    } else {
        throw new InvalidIntegerException("Int out of range");
    }
  }
  


  /* NewLine */
  {LineTerminator}                          { /* ignore */ }

  /* whitespace */
  {WhiteSpace}                              { /* ignore */ }
}

/* error fallback */
[^]                                        { throw new UnknownCharacterException(yytext()); }
