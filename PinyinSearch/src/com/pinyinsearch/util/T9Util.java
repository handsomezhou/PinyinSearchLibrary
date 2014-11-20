package com.pinyinsearch.util;

public class T9Util {
	/**
	 * T9 keyboard 
	 * 1 		2(ABC)	3(DEF) 
	 * 4(GHI) 	5(JKL) 	6(MNO) 
	 * 7(PQRS) 	8(TUV) 	9(WXYZ) 
	 * *		0 		#
	 */

	public static  char getT9Number(char alphabet) {
		char ch = alphabet;

		switch (alphabet) {
		case 'A':
		case 'a':
		case 'B':
		case 'b':
		case 'C':
		case 'c':
			ch = '2';
			break;

		case 'D':
		case 'd':
		case 'E':
		case 'e':
		case 'F':
		case 'f':
			ch = '3';
			break;

		case 'G':
		case 'g':
		case 'H':
		case 'h':
		case 'I':
		case 'i':
			ch = '4';
			break;

		case 'J':
		case 'j':
		case 'K':
		case 'k':
		case 'L':
		case 'l':
			ch = '5';
			break;

		case 'M':
		case 'm':
		case 'N':
		case 'n':
		case 'O':
		case 'o':
			ch = '6';
			break;

		case 'P':
		case 'p':
		case 'Q':
		case 'q':
		case 'R':
		case 'r':
		case 'S':
		case 's':
			ch = '7';
			break;

		case 'T':
		case 't':
		case 'U':
		case 'u':
		case 'V':
		case 'v':
			ch = '8';
			break;

		case 'W':
		case 'w':
		case 'X':
		case 'x':
		case 'Y':
		case 'y':
		case 'Z':
		case 'z':
			ch = '9';
			break;

		default:
			break;
		}

		return ch;
	}
}
