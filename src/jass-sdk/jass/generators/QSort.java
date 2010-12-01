import java.io.*;
import java.lang.*;

  /**
   * Quicksort for strings.  Could not get James Gosling's example working
   * properly, or the "fixed" example, so wrote my own using algorithms
   * book.
   */
//  I grabbed this from the web at the site:
//  http://www.activesw.com/~eric/files/qsort_snippet.java
//  SWN March 98
class QSort  {
  public  QSort(String[] list) {
    quicksort(list, 0, list.length-1);
  }

  private void quicksort(String[] list, int p, int r) {
    if (p < r) {
      int q = partition(list,p,r);
      if (q == r) {
	q--;
      }
      quicksort(list,p,q);
      quicksort(list,q+1,r);
    }
  }

  private int partition (String[] list, int p, int r) {
    String pivot = list[p];
    int lo = p;
    int hi = r;
    
    while (true) {
      while (list[hi].compareTo(pivot) >= 0 &&
lo < hi) {
	hi--;
      }
      while (list[lo].compareTo(pivot) < 0 &&
lo < hi) {
	lo++;
      }
      if (lo < hi) {
	String T = list[lo];
	list[lo] = list[hi];
	list[hi] = T;
      }
      else return hi;
    }
  }      
}

