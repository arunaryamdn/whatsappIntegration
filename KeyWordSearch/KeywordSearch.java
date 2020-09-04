/* arun aryasomayajula 
#This program/code reads all the mobile numbers in a list and sends a whatsapp message with a pre defined template

# the format is something like this - 
#a csv file should be provided, first row of the csv file should be the message you want to send and 
#from second row all the mobile numbers you want to send a message. */

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets; 
import java.nio.file.Files; 
import java.nio.file.Path; 
import java.nio.file.Paths; 
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.util.Scanner;
import java.io.FileWriter;  




public class KeywordSearch {
 
	void KMPSearch(String pat, String txt)
    {
        int M = pat.length();
        int N = txt.length();
 
        int lps[] = new int[M];
        int j = 0; 
 
        computeLPSArray(pat,M,lps);
 
        int i = 0; 
        while (i < N)
        {
            if (pat.charAt(j) == txt.charAt(i))
            {
                j++;
                i++;
            }
            if (j == M)
            {
                System.out.println("Found pattern "+
                              "at index " + (i-j));
                j = lps[j-1];
            }
 
            // mismatch after j matches
            else if (i < N && pat.charAt(j) != txt.charAt(i))
            {
                if (j != 0)
                    j = lps[j-1];
                else
                    i = i+1;
            }
        }
	}
	
	void computeLPSArray(String pat, int M, int lps[])
    {
        // length of the previous longest prefix suffix
        int len = 0;
        int i = 1;
        lps[0] = 0;  // lps[0] is always 0
 
        // the loop calculates lps[i] for i = 1 to M-1
        while (i < M)
        {
            if (pat.charAt(i) == pat.charAt(len))
            {
                len++;
                lps[i] = len;
                i++;
            }
            else  
			{
                if (len != 0)
                {
                    len = lps[len-1];
                }
                else
                {
                    lps[i] = len;
                    i++;
                }
            }
        }
    }
	
	public static void main(String... args) {
		String txt = "ABABDABACDABABCABAB";
        String pat = "ABABCABAB";
        KeywordSearch ks = new KeywordSearch();
		ks.KMPSearch(pat,txt);
	}
}




