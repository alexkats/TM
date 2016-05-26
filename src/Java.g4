grammar Java;

// Start of a grammar
start : packageDeclaration? importLibs* fileType* ;

// Package declaration
packageDeclaration : 'package' pathName ';' ;

// Imports
importLibs : 'import' 'static'? pathName ('.' '*')? ';' ;

// Class, interface or enum declaration
fileType : classModifier* classFile
    | classModifier* interfaceFile
    | classModifier* enumFile
    | ';' ;

// Valid modifiers for method
methodModifier : classModifier
    | 'synchronized'
    | 'transient'
    | 'volatile'
    | 'native' ;

// Class, interface or enum modifiers
classModifier : 'public'
    | 'protected'
    | 'private'
    | 'static'
    | 'abstract'
    | 'final' ;

// Class declaration
classFile : 'class' ValidId generics? ('extends' classType)? ('implements' manyClassTypes)? classBody ;

// Generics
generics : '<' generic (',' generic)* '>' ;

// Generic type
generic : ValidId ('extends' manyGenericClassTypes)? ;

// Generics 'extends' types
manyGenericClassTypes : classType ('&' classType)* ;

// Enum declararion
enumFile : ENUM ValidId ('implements' manyClassTypes)? enumBody ;

// Enum body
enumBody : '{' (enumConstants ';')? classBodyElement* '}' ;

// Enum constants
enumConstants : enumConstant (',' enumConstant)* ;

// Enum constant
enumConstant : ValidId ;

// Interface declaration
interfaceFile : 'interface' ValidId generics? ('extends' manyClassTypes)? interfaceBody ;

// More than one class type
manyClassTypes : classType (',' classType)* ;

// Class body
classBody : '{' classBodyElement* '}' ;

// Interface body
interfaceBody : '{' interfaceBodyElement* '}' ;

// Class body element
classBodyElement : ';'
    | 'static'? codeBlock
    | methodModifier* classMemberDeclaration ;

// Class member declaration
classMemberDeclaration : methodDeclaration
    | genericMethodDeclaration
    | fieldDeclaration
    | constructor
    | genericConstructor
    | interfaceFile
    | classFile
    | enumFile ;

// Method declaration
methodDeclaration : (type | 'void') ValidId parameters ('throws' manyPathNames)? (methodBody | ';') ;

// Generic method declaration
genericMethodDeclaration : generics methodDeclaration ;

// Constructor declaration
constructor : ValidId parameters ('throws' manyPathNames)? constructorBody ;

// Generic constructor declaration
genericConstructor : generics constructor ;

// Field declaration
fieldDeclaration : type varDeclarations ';' ;

// Interface body element
interfaceBodyElement : methodModifier* interfaceMemberDeclaration
    | ';' ;

// Interface member declaration
interfaceMemberDeclaration : constDeclaration
    | interfaceMethodDeclaration
    | interfaceGenericMethodDeclaration
    | interfaceFile
    | classFile
    | enumFile ;

// Const declaration
constDeclaration : type constant (',' constant)* ';' ;

// Const init
constant : ValidId ('[' ']')* '=' varInit ;

// Interface method declaration
interfaceMethodDeclaration : (type | 'void') ValidId parameters ('throws' manyPathNames)? ';' ;

// Interface generic method declaration
interfaceGenericMethodDeclaration : generics interfaceMethodDeclaration ;

// Variable declarations
varDeclarations : varDeclaration (',' varDeclaration)* ;

// Variable declaration
varDeclaration : varId ('=' varInit)? ;

// Variable identifier
varId : ValidId ('[' ']')* ;

// Variable init
varInit : arrayInit
    | expression ;

arrayInit : '{' (varInit (',' varInit)* (',')?)? '}' ;

// Any valid type
type : classType ('[' ']')* | primitiveType ('[' ']')* ;

// Valid class types
classType : ValidId classGenerics? ('.' ValidId classGenerics?)* ;

// Primitives
primitiveType : 'boolean'
    | 'char'
    | 'byte'
    | 'short'
    | 'int'
    | 'long'
    | 'float'
    | 'double' ;

// Generics when using them
classGenerics : '<' classGeneric (',' classGeneric)* '>' ;

// Generic type when using it
classGeneric : classType
    | '?' (('extends' | 'super') classType)? ;

// Many paths to classes
manyPathNames : pathName (',' pathName)* ;

// Parameters in method or constructor
parameters : '(' parametersList? ')' ;

// List of parameters
parametersList : parameter (',' parameter)* (',' lastVectorParameter)?
    | lastVectorParameter ;

// Parameter in method or constructor
parameter : type varId ;

// Last parameter may be something like (String... args)
lastVectorParameter : type '...' varId ;

// Method body
methodBody : codeBlock ;

// Constructor body
constructorBody : codeBlock ;

// Package or import path
pathName : ValidId ('.' ValidId)* ;

value : IntegerValue
    | NumberValue
    | CharValue
    | StringValue
    | BooleanValue
    | 'null' ;

codeBlock : '{' codeLine* '}' ;

codeLine : localVarLine | line | fileType ;

localVarLine : localVar ';' ;

localVar : 'final'? type varDeclarations ;

line : codeBlock
    | 'if' expressionInParen line ('else' line)?
    | 'for' '(' forStart ')' line
    | 'while' expressionInParen line
    | 'synchronized' expressionInParen codeBlock
    | 'return' expression? ';'
    | 'throw' expression ';'
    | 'break' ValidId? ';'
    | 'continue' ValidId? ';'
    | ';'
    | lineExpression ';'
    | ValidId ':' line ;

forStart : forInit? ';' expression? ';' forAfter? ;

forInit : localVar
    | manyExpressions ;

forAfter : manyExpressions ;

expressionInParen : '(' expression ')' ;

manyExpressions : (expression) (',' expression)* ;

lineExpression : expression ;

// Expression
expression : mainExpression
    | expression '.' ValidId
    | expression '.' 'this'
    | expression '[' expression ']'
    | expression '(' manyExpressions? ')'
    | '(' type ')' expression
    | expression ('++' | '--')
    | ('+' | '-' | '++' | '--') expression
    | ('~' | '!') expression
    | expression ('*' | '/' | '%') expression
    | expression ('+' | '-') expression
    | expression ('<' '<' | '>' '>' '>' | '>' '>') expression
    | expression ('<=' | '>=' | '>' | '<') expression
    | expression 'instanceof' type
    | expression ('==' | '!=') expression
    | expression '&' expression
    | expression '^' expression
    | expression '|' expression
    | expression '&&' expression
    | expression '||' expression
    | expression '?' expression ':' expression
    | <assor=right> expression ('=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '>>=' | '>>>=' | '<<=' | '%') expression ;

mainExpression : '(' expression ')'
    | 'this'
    | 'super'
    | value
    | ValidId
    | type '.' 'class'
    | 'void' '.' 'class' ;

ENUM : 'enum' ;

IntegerValue : IntegerNumber ;
fragment IntegerNumber : Sign? Digits;
fragment Digits : Digit+ ;
fragment Digit : [0-9] ;
fragment Sign : [+-] ;

NumberValue : FloatingPointNumber ;
fragment FloatingPointNumber : Sign? Digits ('.' Digits)? ;

BooleanValue : 'true' | 'false' ;

CharValue : '\'' Char '\'' ;
fragment Char : ~['\\] ;

StringValue : '"' StringChars? '"' ;
fragment StringChars : StringChar+ ;
fragment StringChar : ~["\\] ;

// Valid identifier
ValidId : Letter LetterOrDigit* ;

fragment Letter : [a-zA-Z_$] ; // Java letter

fragment LetterOrDigit : [a-zA-Z0-9_$] ; // Java letter or digit

WS : [ \t\r\n]+ -> skip ; // skip all whitespaces
MULTILINE_COMMENT : '/*' .*? '*/' -> skip ; // multiline comments
COMMENT : '//' ~[\r\n]* -> skip ; // single-line comments