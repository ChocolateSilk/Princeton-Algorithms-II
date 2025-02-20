import edu.princeton.cs.algs4.TrieSET;

import java.util.HashSet;
import java.util.Set;
public class BoggleSolver
{
    private final TrieSET dict;
    private final Set<String> prefixSet;
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        dict = new TrieSET();
        prefixSet = new HashSet<>();

        for (int i = 0; i < dictionary.length; i++) {
	    if (dictionary[i].length() >= 3) {
            dict.add(dictionary[i]);
            for (int j = 1; j <= dictionary[i].length(); j++) {
                prefixSet.add(dictionary[i].substring(0, j));
                }
            }
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Set<String> validWords = new HashSet<>();
        int col = board.cols();
        int row = board.rows();
        boolean[][] visited = new boolean[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                dfs(board, i, j, visited, "", validWords);
            }
        }
        return validWords;
    }

    private void dfs(BoggleBoard board, int i, int j, boolean[][] visited, String path, Set<String> validWords) {
        if (i < 0 || i >= board.rows() || j < 0 || j >= board.cols()) {
            return;
        }
        
        if (visited[i][j]) return;

        char letter = board.getLetter(i, j);
        String word = path + (letter == 'Q' ? "QU" : letter);

        if (!prefixSet.contains(word)) return;

        if (word.length() >= 3 && dict.contains(word)) {
            validWords.add(word);
        }

        visited[i][j] = true;

        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                if (di == 0 && dj == 0) continue;
                int ni = i + di;
                int nj = j + dj;
                if (ni >= 0 && ni < board.rows() && nj >= 0 && nj < board.cols()) {
                    dfs(board, ni, nj, visited, word, validWords);
                }
            }
        }

        visited[i][j] = false;
    }
    
    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!dict.contains(word)) return 0;

        int len = word.length();
        if (len == 3 || len == 4) return 1;
        if (len == 5) return 2;
        if (len == 6) return 3;
        if (len == 7) return 5;
        else return 11;
    }

}