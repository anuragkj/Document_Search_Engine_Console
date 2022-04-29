import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Anurag, Harsh, Jay
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* 
	 * Adding the toString function
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		// COMPLETE THIS METHOD
		HashMap<String, Occurrence> map = new HashMap<String, Occurrence>(100,2f);
		Scanner sc = new Scanner(new File(docFile));
		while(sc.hasNext())
		{
			String word = sc.next();
			word = this.getKeyWord(word);
			if(word==null)
				continue;
		
		//if its not a noise word
		if(!noiseWords.containsKey(word))
		{
			if(!map.containsKey(word))
			{
				Occurrence occur = new Occurrence(docFile,1);
				map.put(word, occur);
			}
			else if(map.containsKey(word))
				{
				Occurrence temp = map.get(word);
				temp.frequency++;
				map.put(word,temp);
				
				}
			}
		
		}
		
		
		return map;
		
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		// COMPLETE THIS METHOD
		
		Set<String> set = kws.keySet();
		Iterator<String> iterator = set.iterator();
		while(iterator.hasNext())
		{
			String key = iterator.next();
			Occurrence occur = kws.get(key);
			ArrayList<Occurrence> list = keywordsIndex.get(key);
			if (list == null)
			{
				list = new ArrayList<Occurrence>();
				keywordsIndex.put(key, list);
			}
			list.add(occur);
			this.insertLastOccurrence(list);
		}
	}
	
	
	public String getKeyWord(String word)
	{
		if (word == null)
		{
			return null;
		}
		
		word = word.toLowerCase();
		String alpha = "abcdefghijklmnopqrstuvwxyz";
		
		while(true)
		{
			int w = word.length()-1;
			if (word.charAt(w) == '.' || word.charAt(w) == ',' || word.charAt(w) == '?' || word.charAt(w) == ':' || word.charAt(w) == ';' || word.charAt(w) == '!')
			{
				if (w != 0)
					word = word.substring(0,w);
				else
					return null;
			}
			else
				break;
			}
		
	
		boolean checkNoise = noiseWords.containsKey(word);
		
		if (checkNoise)
			return null;
		
		int i=0;
		for (int w = 0 ; w < word.length();w++)
		{
			for (i = 0 ; i < alpha.length() ; i++)
			{
				if (word.charAt(w) == alpha.charAt(i))
					break;
			}
				if (i == alpha.length())
				{
					return null;
				}
					
				i=0;
		}
		
		return word;
	}
		
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// COMPLETE THIS METHOD
		ArrayList<Integer> integerList = new ArrayList<Integer>();
		
		if(occs.size()==1)
		{
			integerList.add(0);
			return integerList;	
		}
		
		int indices [] = new int[occs.size()-1];
		for (int i = 0 ; i < occs.size()-1;i++)
		{
			Occurrence occur = occs.get(i);
			indices[i] = occur.frequency;
		}
				
		int target = occs.get(occs.size()-1).frequency;
		
		int low=0;
		int high=indices.length-1;
		
		while ( low<= high)
		{
			int mid = (low + high)/2;
			if (indices[mid] == target)
			{
				integerList.add(mid);
				break;
			}
			else if (indices[mid] < target)
				high = mid-1;
			else if (indices[mid] > target)
				low = mid+1;
			if((low<=high) == false)
				break;
			integerList.add(mid);
		}
		
		int secondLast = occs.get(occs.size()-2).frequency;
		if (integerList.size()==0)
			integerList.add(0);
		
		if (!(target < secondLast))
		{
			Occurrence o = occs.remove(occs.size()-1);
			int location = integerList.get(integerList.size()-1);
			occs.add(location, o);
		}
		
		return integerList;
		
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */

	
	public ArrayList<String> top5search(String kw1, String kw2) 
	{
		ArrayList<String> top5 = new ArrayList<String>();
		String KW1 = getKeyWord(kw1); 
		System.out.println("KW1: " + KW1);
		String KW2 = getKeyWord(kw2);
		System.out.println("KW2: " + KW2);
		ArrayList<Occurrence> kw1List = keywordsIndex.get(KW1);
		System.out.println("kw1List: " + kw1List);
		ArrayList<Occurrence> kw2List = keywordsIndex.get(KW2);
		System.out.println("kw2List: " + kw2List);
		if ((KW1 == null && KW2 == null)||(kw1List == null && kw2List == null))
		{ // if kw1 and kw2 both are not keywords OR they are both keywords but they're not in the master hash
			System.out.println("both words aren't keywords OR they're null");
			return null;
		}
		
		if (KW1 != null && kw1List != null && kw2List == null)
		{ // kw1 is a keyword, and it's in the master hash (KW2 might not be null, but it isn't in the master hash)
		//	System.out.println("KW1 is a keyword and it's in the master hash");
			for (int i = 0; i < kw1List.size() && (top5.size() < 5 || top5.size() != 5); i++)
			{ // putting Occurrence keys in top5 arraylist
				top5.add(kw1List.get(i).document); // add document in top5
				System.out.println("top5 after addition: " + top5);
			}
			System.out.println("the top5 are: " + top5);
			return top5;
		} 
		
		if (KW2 != null && kw2List != null && kw1List == null)
		{ // kw2 is a keyword, and it's in the master hash (KW1 might not be null, but it isn't in the master hash)
		//	System.out.println("KW2 is a keyword and it's in the master hash");
			for (int i = 0; kw2List != null &&i < kw2List.size() && (top5.size() < 5 || top5.size() != 5); i++)
			{ // putting Occurrence keys in top5 arraylist
				top5.add(kw2List.get(i).document); // add document in top5
				System.out.println("top5 after addition: " + top5);
			}
			System.out.println("the top5 are: " + top5);
			return top5;
		}
		
			while ((kw1List.isEmpty() == false || kw2List.isEmpty() == false) && (top5.size() < 5 || top5.size() != 5))
			{
				Occurrence word1 = null; Occurrence word2 = null;
				int c1 = 0; int c2 = 0;
				if (kw2List.isEmpty() == false)
				{
					word2 = kw2List.get(0);
					c2 = word2.frequency;
				}
				if (kw1List.isEmpty() == false)
				{
					word1 = kw1List.get(0);
					c1 = word1.frequency;
				}
				System.out.println("c1 is: " + c1);
				System.out.println("c2 is: " + c2);
				if (c2 > c1){
				System.out.println("c2 > c1");
					top5.add(word2.document);
				System.out.println("doc added: " + word2.document);
					//remove word2's document name from kw2List
					int i = 0;
					while (i < kw2List.size())
					{
						if (kw2List.get(i).document.equals(word2.document))
						{ // if document names match
						System.out.println("removing word2's doc from kw2List");
							System.out.println(kw2List.get(i));
							kw2List.remove(i); // remove the object from arraylist
							System.out.println(kw2List);
						} else 
						{ //object's document name != word2's document name
							i++; //go on to next object
						}
					}
					
					//remove word2's document name from kw1List
					i = 0;
					while (i < kw1List.size())
					{
						if (kw1List.get(i).document.equals(word2.document)){ // if document names match
							// System.out.println("removing word2's doc from kw1List");
							System.out.println(kw1List.get(i));
							kw1List.remove(i);
						} else {
							i++;
						}
					}}
				else if (c1 > c2){
					System.out.println("c1 > c2");
					top5.add(word1.document);
					System.out.println("word1's doc added: " + word1.document);
					//remove word1's document name from kw1List
					int i = 0;
					while (i < kw1List.size()){
						if (kw1List.get(i).document.equals(word1.document)){ // if document names match
						//	System.out.println("removing word1's doc from kw1List");
							System.out.println(kw1List.get(i));
							kw1List.remove(i);
						} else {
							i++;
						}
					}
					//remove word1's document name from kw2List
					i = 0;
					while (i < kw2List.size()){
						if (kw2List.get(i).document.equals(word1.document)){ // if document names match
					//		System.out.println("removing word1's doc from kw2List");
							System.out.println(kw2List.get(i));
							kw2List.remove(i);
						} else {
							i++;
						}
					}}
				else if (c1 == c2){ // word1 and word2 have the same frequency
					System.out.println("c1 == c2");
					if (!(word1.document.equals(word2.document))){ // if both words have different doc names
						top5.add(word1.document); // first, add the document of the first object
						System.out.println("added doc of first word: " + word1.document);
						top5.add(word2.document); // then, add the document of the second object
						System.out.println("added doc of second word: " + word2.document);
						//remove the doc names from lists
						//remove word1's document name from kw1List
						int i = 0;
					
						while (i < kw1List.size()){
							if (kw1List.get(i).document.equals(word1.document)){ // if document names match
								System.out.println("in kw1, object's doc == word's doc, removing");
								System.out.println(kw1List.get(i));
								kw1List.remove(i);
								
							} else {
								i++;
							}
						}
						//System.out.println("removed all of word1's docs from kw1List");
						//remove word1's document name from kw2List
						i = 0;
						while (i < kw2List.size()){
							if (kw2List.get(i).document.equals(word1.document)){ // if document names match
								System.out.println("in kw2, object's doc == word's doc, removing");
								kw2List.remove(i);
							} else {
								i++;
							}
						}
						
						//remove word2's document name from kw2List
						i = 0;
						while (i < kw2List.size()){
							if (kw2List.get(i).document.equals(word2.document)){ // if document names match
								kw2List.remove(i); // remove the object from arraylist
							} else { //object's document name != word2's document name
								i++; //go on to next object
							}
						}
						//remove word2's document name from kw1List
						i = 0;
						while (i < kw1List.size()){
							if (kw1List.get(i).document.equals(word2.document)){ // if document names match
								kw1List.remove(i);
							} else {
								i++;
							}
						}
					} else { // words have same doc name
						top5.add(word1.document);
						//remove the doc's name from lists
						//remove word1's document name from kw1List
						int i = 0;
						
						while (i < kw1List.size()){
							if (kw1List.get(i).document.equals(word1.document)){ // if document names match
								kw1List.remove(i);
							} else {
								i++;
							}
						}
						//remove word1's document name from kw2List
						i = 0;
						while (i < kw2List.size()){
							if (kw2List.get(i).document.equals(word1.document)){ // if document names match
								kw2List.remove(i);
							} else {
								i++;
							}
						}
						
					}
				}
				System.out.println("kw1List: " + kw1List);
				System.out.println("kw2List: " + kw2List);
				//System.out.println("restarting main while loop");
			}
			
			if (top5.size() == 0){
				System.out.println("top5 is empty: " + top5);
				System.out.println();
				return null;
			} 
			else 
			{
				//System.out.println("reached the end");
				System.out.println("the top5 are: " + top5);
				System.out.println();
				return top5;
			}
	}

    public static void main(String[] args) throws IOException
	{

		
		// TODO Auto-generated method stub
		
	
		
		System.out.println("Enter doument title ");
		String docs = br.readLine();
		System.out.println("Enter noise words file ");
		String noise = br.readLine();
		LittleSearchEngine lol = new LittleSearchEngine();
		lol.makeIndex(docs, noise);
		System.out.println("Enter first search word ");
		String first = br.readLine();
		System.out.println("Enter second search world ");
		String second = br.readLine();
		System.out.println(lol.top5search(first, second));
		

	}
}


