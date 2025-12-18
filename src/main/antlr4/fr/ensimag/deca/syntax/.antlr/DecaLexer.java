// Generated from /home/gl/projet_gl/src/main/antlr4/fr/ensimag/deca/syntax/DecaLexer.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class DecaLexer extends AbstractDecaLexer {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		IDENT=1, PLUS=2, MINUS=3, TIMES=4, INT=5, WS=6, COMMENT=7, LINE_COMMENT=8, 
		STRING=9, OBRACE=10, PRINTLN=11, OPARENT=12, CPARENT=13, SEMI=14, CBRACE=15;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"LETTER", "DIGIT", "IDENT", "PLUS", "MINUS", "TIMES", "INT", "WS", "COMMENT", 
			"LINE_COMMENT", "STRING", "OBRACE", "PRINTLN", "OPARENT", "CPARENT", 
			"SEMI", "CBRACE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'+'", "'-'", "'*'", null, null, null, null, null, "'{'", 
			"'println'", "'('", "')'", "';'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "IDENT", "PLUS", "MINUS", "TIMES", "INT", "WS", "COMMENT", "LINE_COMMENT", 
			"STRING", "OBRACE", "PRINTLN", "OPARENT", "CPARENT", "SEMI", "CBRACE"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}




	public DecaLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "DecaLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 7:
			WS_action((RuleContext)_localctx, actionIndex);
			break;
		case 8:
			COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		case 9:
			LINE_COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:

			              skip(); // avoid producing a token
			          
			break;
		}
	}
	private void COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1:
			 skip(); 
			break;
		}
	}
	private void LINE_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 2:
			 skip(); 
			break;
		}
	}

	public static final String _serializedATN =
		"\u0004\u0000\u000f\u0091\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0004\u0002)\b\u0002\u000b\u0002"+
		"\f\u0002*\u0001\u0002\u0004\u0002.\b\u0002\u000b\u0002\f\u0002/\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0004\u00025\b\u0002\u000b\u0002\f\u0002"+
		"6\u0001\u0002\u0004\u0002:\b\u0002\u000b\u0002\f\u0002;\u0001\u0002\u0004"+
		"\u0002?\b\u0002\u000b\u0002\f\u0002@\u0001\u0002\u0001\u0002\u0005\u0002"+
		"E\b\u0002\n\u0002\f\u0002H\t\u0002\u0001\u0003\u0001\u0003\u0001\u0004"+
		"\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0004\u0006Q\b\u0006"+
		"\u000b\u0006\f\u0006R\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0005\b\\\b\b\n\b\f\b_\t\b\u0001\b\u0001\b\u0001\b"+
		"\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0005\tj\b\t\n\t\f\t"+
		"m\t\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n"+
		"\u0001\n\u0001\n\u0005\ny\b\n\n\n\f\n|\t\n\u0001\n\u0001\n\u0001\u000b"+
		"\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001"+
		"\u0010\u0001\u0010\u0001]\u0000\u0011\u0001\u0000\u0003\u0000\u0005\u0001"+
		"\u0007\u0002\t\u0003\u000b\u0004\r\u0005\u000f\u0006\u0011\u0007\u0013"+
		"\b\u0015\t\u0017\n\u0019\u000b\u001b\f\u001d\r\u001f\u000e!\u000f\u0001"+
		"\u0000\u0003\u0002\u0000AZaz\u0003\u0000\t\n\r\r  \u0002\u0000\n\n\r\r"+
		"\u009c\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000"+
		"\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000"+
		"\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000"+
		"\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000"+
		"\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000"+
		"\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000"+
		"\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000"+
		"!\u0001\u0000\u0000\u0000\u0001#\u0001\u0000\u0000\u0000\u0003%\u0001"+
		"\u0000\u0000\u0000\u0005(\u0001\u0000\u0000\u0000\u0007I\u0001\u0000\u0000"+
		"\u0000\tK\u0001\u0000\u0000\u0000\u000bM\u0001\u0000\u0000\u0000\rP\u0001"+
		"\u0000\u0000\u0000\u000fT\u0001\u0000\u0000\u0000\u0011W\u0001\u0000\u0000"+
		"\u0000\u0013e\u0001\u0000\u0000\u0000\u0015p\u0001\u0000\u0000\u0000\u0017"+
		"\u007f\u0001\u0000\u0000\u0000\u0019\u0081\u0001\u0000\u0000\u0000\u001b"+
		"\u0089\u0001\u0000\u0000\u0000\u001d\u008b\u0001\u0000\u0000\u0000\u001f"+
		"\u008d\u0001\u0000\u0000\u0000!\u008f\u0001\u0000\u0000\u0000#$\u0007"+
		"\u0000\u0000\u0000$\u0002\u0001\u0000\u0000\u0000%&\u000209\u0000&\u0004"+
		"\u0001\u0000\u0000\u0000\')\u0003\u0001\u0000\u0000(\'\u0001\u0000\u0000"+
		"\u0000)*\u0001\u0000\u0000\u0000*(\u0001\u0000\u0000\u0000*+\u0001\u0000"+
		"\u0000\u0000+-\u0001\u0000\u0000\u0000,.\u0005$\u0000\u0000-,\u0001\u0000"+
		"\u0000\u0000./\u0001\u0000\u0000\u0000/-\u0001\u0000\u0000\u0000/0\u0001"+
		"\u0000\u0000\u000001\u0001\u0000\u0000\u000012\u0005_\u0000\u00002F\u0001"+
		"\u0000\u0000\u000035\u0003\u0001\u0000\u000043\u0001\u0000\u0000\u0000"+
		"56\u0001\u0000\u0000\u000064\u0001\u0000\u0000\u000067\u0001\u0000\u0000"+
		"\u000079\u0001\u0000\u0000\u00008:\u0003\u0003\u0001\u000098\u0001\u0000"+
		"\u0000\u0000:;\u0001\u0000\u0000\u0000;9\u0001\u0000\u0000\u0000;<\u0001"+
		"\u0000\u0000\u0000<>\u0001\u0000\u0000\u0000=?\u0005$\u0000\u0000>=\u0001"+
		"\u0000\u0000\u0000?@\u0001\u0000\u0000\u0000@>\u0001\u0000\u0000\u0000"+
		"@A\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000BC\u0005_\u0000\u0000"+
		"CE\u0001\u0000\u0000\u0000D4\u0001\u0000\u0000\u0000EH\u0001\u0000\u0000"+
		"\u0000FD\u0001\u0000\u0000\u0000FG\u0001\u0000\u0000\u0000G\u0006\u0001"+
		"\u0000\u0000\u0000HF\u0001\u0000\u0000\u0000IJ\u0005+\u0000\u0000J\b\u0001"+
		"\u0000\u0000\u0000KL\u0005-\u0000\u0000L\n\u0001\u0000\u0000\u0000MN\u0005"+
		"*\u0000\u0000N\f\u0001\u0000\u0000\u0000OQ\u0003\u0003\u0001\u0000PO\u0001"+
		"\u0000\u0000\u0000QR\u0001\u0000\u0000\u0000RP\u0001\u0000\u0000\u0000"+
		"RS\u0001\u0000\u0000\u0000S\u000e\u0001\u0000\u0000\u0000TU\u0007\u0001"+
		"\u0000\u0000UV\u0006\u0007\u0000\u0000V\u0010\u0001\u0000\u0000\u0000"+
		"WX\u0005/\u0000\u0000XY\u0005*\u0000\u0000Y]\u0001\u0000\u0000\u0000Z"+
		"\\\t\u0000\u0000\u0000[Z\u0001\u0000\u0000\u0000\\_\u0001\u0000\u0000"+
		"\u0000]^\u0001\u0000\u0000\u0000][\u0001\u0000\u0000\u0000^`\u0001\u0000"+
		"\u0000\u0000_]\u0001\u0000\u0000\u0000`a\u0005*\u0000\u0000ab\u0005/\u0000"+
		"\u0000bc\u0001\u0000\u0000\u0000cd\u0006\b\u0001\u0000d\u0012\u0001\u0000"+
		"\u0000\u0000ef\u0005/\u0000\u0000fg\u0005/\u0000\u0000gk\u0001\u0000\u0000"+
		"\u0000hj\b\u0002\u0000\u0000ih\u0001\u0000\u0000\u0000jm\u0001\u0000\u0000"+
		"\u0000ki\u0001\u0000\u0000\u0000kl\u0001\u0000\u0000\u0000ln\u0001\u0000"+
		"\u0000\u0000mk\u0001\u0000\u0000\u0000no\u0006\t\u0002\u0000o\u0014\u0001"+
		"\u0000\u0000\u0000pz\u0005\"\u0000\u0000qy\u0003\u0001\u0000\u0000rs\u0005"+
		"\\\u0000\u0000sy\u0005\"\u0000\u0000tu\u0005\\\u0000\u0000uy\u0005\\\u0000"+
		"\u0000vy\u0003\u000f\u0007\u0000wy\u0003\r\u0006\u0000xq\u0001\u0000\u0000"+
		"\u0000xr\u0001\u0000\u0000\u0000xt\u0001\u0000\u0000\u0000xv\u0001\u0000"+
		"\u0000\u0000xw\u0001\u0000\u0000\u0000y|\u0001\u0000\u0000\u0000zx\u0001"+
		"\u0000\u0000\u0000z{\u0001\u0000\u0000\u0000{}\u0001\u0000\u0000\u0000"+
		"|z\u0001\u0000\u0000\u0000}~\u0005\"\u0000\u0000~\u0016\u0001\u0000\u0000"+
		"\u0000\u007f\u0080\u0005{\u0000\u0000\u0080\u0018\u0001\u0000\u0000\u0000"+
		"\u0081\u0082\u0005p\u0000\u0000\u0082\u0083\u0005r\u0000\u0000\u0083\u0084"+
		"\u0005i\u0000\u0000\u0084\u0085\u0005n\u0000\u0000\u0085\u0086\u0005t"+
		"\u0000\u0000\u0086\u0087\u0005l\u0000\u0000\u0087\u0088\u0005n\u0000\u0000"+
		"\u0088\u001a\u0001\u0000\u0000\u0000\u0089\u008a\u0005(\u0000\u0000\u008a"+
		"\u001c\u0001\u0000\u0000\u0000\u008b\u008c\u0005)\u0000\u0000\u008c\u001e"+
		"\u0001\u0000\u0000\u0000\u008d\u008e\u0005;\u0000\u0000\u008e \u0001\u0000"+
		"\u0000\u0000\u008f\u0090\u0005}\u0000\u0000\u0090\"\u0001\u0000\u0000"+
		"\u0000\f\u0000*/6;@FR]kxz\u0003\u0001\u0007\u0000\u0001\b\u0001\u0001"+
		"\t\u0002";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}