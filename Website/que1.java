/*
 *  VCST.java
 *  Assignment #2, Question #1
 *  Computer Science 3711, Winter 2003
 *
 *	Written by Todd Wareham, early February, 2003
 *
 *	Program implements the dynamic-pruning DFS combinatorial
 *	 solution-space tree (CST) algorithm given in the 
 *	 class notes to find the minimum-sized vertex cover of a
 *       given graph. There is an additional command-line argument
 *       that specifies whether details of the full tree-traversal
 *       ("full") or just the optimal solutions ("min") are
 *       displayed.
 */

import java.io.*;

    

class VCST {


    public static void main(String[] arg) throws IOException {

        InstanceVC I;
        SolutionVC S;
	    ValInt optVal, numOptSol;
        boolean fullDisplay;


        if (arg.length != 2) {
            System.out.println("format: VCST <graph-file>" + 
                                            "{\"full\"|\"min\"}");
	    return;
        }

	if (arg[1].equals("full"))
	    fullDisplay = true;
	else if (arg[1].equals("min"))
	    fullDisplay = false;
	else {
	    System.out.println("error: bad display-type");
	    return;
	}

		/*
		 *  Read in and display instance graph.
		 */

	I = new InstanceVC(arg[0]);

	System.out.println("\nUInput graph description:\n");
	I.display("\t");

		/*
		 *  Determine the size of the minimum vertex cover of
                 *   the given graph.
		 */

        S =  new SolutionVC(I);

        if (fullDisplay) 
	    System.out.println("\nSolution Tree Traversal:\n");
	    optVal = new ValInt(Integer.MAX_VALUE);
        DFSVC_OptVal(0, S, I, optVal, fullDisplay);
        if (fullDisplay) 
            System.out.println();

        System.out.println("Optimal value = " + optVal.getVal()+ "\n");

		/*
		 *  Print all minimum vertex covers of the given graph.
		 *  Note that as we already know the optimal solution
		 *   value at this point, this tree traversal is faster,
		 *   i.e., more intermediate nodes can be pruned during
		 *   the traversal.
	         */

        if (optVal.getVal() == Integer.MAX_VALUE)
	    System.out.println(">> No solutions");
        else {
	    numOptSol = new ValInt(0);
            S = new SolutionVC(I);
            DFSVC_OptSol(0, S, I, optVal, numOptSol, fullDisplay);
	    System.out.println("\n>> " + numOptSol.getVal() + 
			       " optimal solution(s)");
        }
        System.out.println();

    }  // End of main method


/*
 *  Given a graph I, a partial vertex cover S for that graph, the size
 *   OPTVAL of the smallest vertex cover seen so far, and a vertex # i,
 *   this procedure implicitly constructs the combinatorial solution-
 *   space tree relative to addition and non-addition of vertex i to
 *   the partial solution in order to find the size of the 
 *   minimum vertex cover of the given graph. If variable FULLDISPLAY is
 *   TRUE, full details of the tree trtaversal are printed.
 *  The algorithm used below has been simplified and modified
 *   from that given in the class notes to aid display of the 
 *   tree nodes during the traversal.
 */

    public static void DFSVC_OptVal(int i, SolutionVC S, InstanceVC I, 
		                    ValInt optVal, boolean fullDisplay){
        String prefix;

        if (!S.isPotOpt(I,i, optVal.getVal())) {
            prefix = "\t" + blankString(2 * i) + "  XP <" + i + "> ";
            if (fullDisplay) S.display(prefix, optVal.getVal());
        }
        else if (S.isFullSol()) {
            if (!S.isViable()) {
                prefix = "\t" + blankString(2 * i) + "  XV <" + 
			   i + "> ";
            	if (fullDisplay) S.display(prefix, optVal.getVal());
            }
	    else {
                prefix = "\t" + blankString(2 * i) + "  LL <" + 
			   i + "> ";
            	if (fullDisplay) S.display(prefix, optVal.getVal());
		optVal.setVal(optVal.getVal() > S.getSolVal()? 
			        S.getSolVal(): optVal.getVal());
            }
        }
        else {
            prefix = "\t" + blankString(2 * i) + "  -- <" + i + "> ";
            if (fullDisplay) S.display(prefix, optVal.getVal());
            DFSVC_OptVal(i + 1, S.inc(), I, optVal, fullDisplay);
	    DFSVC_OptVal(i + 1, S.addInc(i), I, optVal, fullDisplay);
        }
    }  // End of procedure DFSVC_OptVal


/*
 *  Given a graph I, a partial vcertex cover S for that graph, the size 
 *   OPTVAL of the minimum vertex cover, and the number NUMOPTSOL of
 *   minimum vertex covers seen so far, this procedure implicitly 
 *   constructs the combinatorial solution-space tree in order to print
 *   all minimum vertex covers of the given graph. If variable 
 *   FULLDISPLAY is TRUE, full details of the tree trtaversal are 
 *   printed; otherwise, only the minimum vertex covers are printed.
 */

    public static void DFSVC_OptSol(int i, SolutionVC S, InstanceVC I,
				    ValInt optVal, ValInt numOptSol,
				    boolean fullDisplay){
        String prefix;

        if (!S.isPotOpt(I,i, optVal.getVal())) {
            prefix = "\t" + blankString(2 * i) + "  XP <" + i + "> ";
            if (fullDisplay) S.display(prefix, optVal.getVal());
        }
        else if (S.isFullSol()) {
            if (!S.isViable()) {
                prefix = "\t" + blankString(2 * i) + "  XV <" + 
			   i + "> ";
            	if (fullDisplay) S.display(prefix, optVal.getVal());
            }
	    else if (S.getSolVal() == optVal.getVal()) {
		numOptSol.setVal(numOptSol.getVal() + 1);
                prefix = "\t" + numOptSol.getVal() + 
		           blankString(2 * i) + ">> <" + i + "> ";
            	S.display(prefix, optVal.getVal());
	    }
	    else {
                prefix = "\t" + blankString(2 * i) + "  LL <" + 
			   i + "> ";
            	if (fullDisplay) S.display(prefix, optVal.getVal());
            }
        }
        else {
            prefix = "\t" + blankString(2 * i) + "  -- <" + i + "> ";
            if (fullDisplay) S.display(prefix, optVal.getVal());
            DFSVC_OptSol(i + 1, S.inc(), I, optVal, numOptSol, 
		         fullDisplay);
	    DFSVC_OptSol(i + 1, S.addInc(i), I, optVal, numOptSol, 
		         fullDisplay);
        }
    }  // End of procedure DFSVC_OptSol


/*
 *  Procedure returns a string of N blanks.
 */

    public static String blankString(int n){
	String str = "";
        for (int i = 0; i < n; i++)
	    str += " ";
        return(str);
    }  // End of procedure BLANKSTRING

} // End of class VCST



/*
 *  Class InstanceVC
 *   Stores variables associated with an instance of the VERTEX COVER
 *    problem.
 *
 *	vName	Vertex names
 *	adj	Graph adjacancy matrix
 *	curEi, curEj
 *		Counters assocaited with enumeration of all edges in
 *		 the graph
 *	curVEi, curVEj
 *		Counters assocaited with enumeration of all edges in
 *		 the graph adjacent to a particular vertex
 */

class InstanceVC {

    private String[] vName;
    private boolean[][] adj;
    private int curEi = -1, curEj = -1, curVEi = -1, curVEj = -1;


/*
 *  Construct a VERTEX CIOVER instance by reading ina description of
 *   a graph from file FILKENAME.
 */

    public InstanceVC(String filename) throws IOException {
	String line;
	int    i, j, numVertices;

	BufferedReader inFile = 
		new BufferedReader(new FileReader(filename));
	numVertices = Integer.parseInt(inFile.readLine());
	vName = new String[numVertices];
	adj = new boolean[numVertices][numVertices];
	for (i = 0; i < numVertices; i++)
	    vName[i] = inFile.readLine();
	for (i = 0; i < numVertices; i++) {
	    line = inFile.readLine();
	    for (j = 0; j < numVertices; j++)
		adj[i][j] = (line.charAt(j) == 'T'? true: false);
	}
    }  // End of InstanceVC constructor


/*
 *  Return the number of vertices in the graph.
 */

    public int numVertices(){
	return(vName.length);
    }  // End of method numVertices


/*
 *  Start an enumeration of all edges in the graph.
 */

    public void startEdgeEnum(){
	curEi = 1;
        curEj = -1;
    }  // End of method startEdgeEnum


/*
 *  Determine if there is a nother edge in the enumeration of all edges
 *   in the graph.
 */

    public boolean nextEdgeEnum(){
	boolean finished, found;

	found = finished = false;
	while (!finished && !found){
	    curEj++;
	    if (curEj > (curEi - 1)) {
		curEi++;
		curEj = 0;
	    }
	    if (curEi >= vName.length)
		finished = true;
	    else if (adj[curEi][curEj])
		found = true;
	}
	return(found);
    }  // End of method nextEdgeEnum


/*
 *  Return the i-coordinate of the current edge in the enumeration of 
 *   all edges in the graph; if there is no valid current edge,
 *   return -1.
 */

    public int curEdgeU(){
	return(curEj == -1? -1: curEi);
    }  // End of method curEdgeU


/*
 *  Return the j-coordinate of the current edge in the enumeration of 
 *   all edges in the graph; if there is no valid current edge,
 *   return -1.
 */

    public int curEdgeV(){
	return(curEj);
    }  // End of method curEdgeV


/*
 *  Start an enumeration of all edges in the graph that are adjacent to
 *   vertex V.
 */

    public void startVEdgeEnum(int v){
	curVEi = v;
        curVEj = -1;
    }  // End of method startVEdgeEnum


/*
 *  Determine if there is a nother edge in the enumeration of all edges
 *   in the graph that are adjacent to vertex V.
 */

    public boolean nextVEdgeEnum(int v){
	boolean finished, found;

	found = finished = false;
	while ((!finished) && (!found)){
	    curVEj++;
	    if (curVEj >= vName.length)
		finished = true;
	    else if (adj[curVEi][curVEj])
		found = true;
	}
	return(found);
    }  // End of method nextVEdgeEnum


/*
 *  Return the i-coordinate of the current edge in the enumeration of 
 *   all edges in the graph that are adjacent to vertex V; if there is 
 *   no valid current edge, return -1.
 */

    public int curVEdgeU(int v){
	return(curVEj == -1? -1: curVEi);
    }  // End of method curVEdgeU


/*
 *  Return the j-coordinate of the current edge in the enumeration of 
 *   all edges in the graph that are adjacent to vertex V; if there is 
 *   no valid current edge, return -1.
 */

    public int curVEdgeV(int v){
	return(curVEj);
    }  // End of method curVEdgeV


/*
 *  Display the graph associated with this instance such that each
 *   line starts with string PREFIX.
 */

    public void display(String prefix){
	int i, j;

	for (i = 0; i < vName.length; i++)
	    System.out.println(prefix + "Vertex #" + (i + 1) + ": " + 
                                        vName[i]);
	System.out.print("\n" + prefix + "      ");
	for (i = 0; i < vName.length; i++)
	    System.out.print(" V ");
	System.out.print("\n" + prefix + "      ");
	for (i = 0; i < vName.length; i++)
	    System.out.print(" " + (i + 1) + " " );
	System.out.println("\n");
	for (i = 0; i < vName.length; i++) {
	    System.out.print(prefix + "  V" + (i + 1) + ": ");
	    for (j = 0; j < vName.length; j++)
		System.out.print(adj[i][j]? " X ": " - ");
	    System.out.println("\n");
	}
    }  // End of method display

}  // End of class InstanceVC



/*
 *  Class SolutionVC
 *   Stores variables associated with a solution to the VERTEX COVER
 *    problem.
 *
 *      I      Associated VERTEX COVER instance
 *	n      Maximum possible number of vertices in 
 *		solution / number of vertices in instance
 *	lvertex  Number of vertices included in solution	
 *	inc    Inclusion status of vertex i
 *
 */

class SolutionVC {

    private InstanceVC I;
    private boolean[]  inc;
    private int        n, lvertex;


/*
 *  Given a VERTEX COVER instance I, create and return an
 *   empty solution for that instance.
 */

    public SolutionVC(InstanceVC I){

        this.I = I;
	n = I.numVertices();
	inc = new boolean[n];
        for (int i = 0; i < n; i++)
	    inc[i] = false;
	lvertex = -1;
    }  // End of SolutionVC constructor


/*
 *  Return the size of, i.e., the number of vertices in, the solution.
 */

    public int getSolVal(){
	int titem = 0;
	for (int i = 0; i <= lvertex; i++)
	    if (inc[i])
		titem++;
	return(titem);
    }  // End of method getSolVal


/*
 *  Determine if the solution is  full solution.
 */

    public boolean isFullSol(){
	return(lvertex == (n - 1)? true: false);
    }  // End of method numOptSol


/*
 *  Return a copy of the solution to which no vertex has been added.
 */

    public SolutionVC inc(){
        SolutionVC Sp = this.copy();
	Sp.lvertex++;
        return(Sp);
    }  // End of method inc


/*
 *  Return a copy of the solution to which vertex i has been added.
 */

    public SolutionVC addInc(int i){
        SolutionVC Sp = this.copy();
	Sp.inc[i] = true;
	Sp.lvertex++;
        return(Sp);
    }  // End of method addInc


/*
 *  Return a copy of the solution.
 */

    private SolutionVC copy(){
        SolutionVC Sp = new SolutionVC(this.I);;

    	Sp.lvertex = lvertex;
        for (int i = 0; i < n; i++)
	    Sp.inc[i] = inc[i];
        return(Sp);
    }  // End of method copy


/*
 *  Determine if the solution is viable, i.e., does the stored set
 *   of vertices form a vertex cover of the graph?
 */

    public boolean isViable(){
	I.startEdgeEnum();
	while (I.nextEdgeEnum())
	    if (!(inc[I.curEdgeU()] || inc[I.curEdgeV()]))
		return(false);
	return(true);
    }  // End of method isViable


/*
 *  Determine if the solution is potentially optimal, i.e., it is of
 *   size less than OPTVAL (the best vertex cover seen so far) and
 *   all edges adjacent to vertex i are not already in the partial 
 *   vertex cover encoded in the solution.
 */

    public boolean isPotOpt(InstanceVC I, int i, int optVal){

	if (getSolVal() > optVal) return (false);

	if (i == 0)
	    return(true);
	else {
	    i = i - 1;
	    I.startVEdgeEnum(i);
	    while (I.nextVEdgeEnum(i)) {
	        if (!(inc[I.curVEdgeU(i)] && inc[I.curVEdgeV(i)]))
		    return(true);
	    }
	    return(false);
	}
    }  // End of method isPotOpt    


/*
 *  Display the solution such that each line starts with string PREFIX.
 */

    public void display(String prefix, int optVal){

        System.out.print(prefix + "[");
        int titem = 0;
        for (int i = 0; i < n; i++) {
	    if (inc[i]) {
	        titem++;
	        if (titem == 1)
		    System.out.print(i + 1);
	        else
		    System.out.print(", " + (i + 1));
	    }
        }
        System.out.println("] (" + titem + "|" + optVal + ")");
    }  // End of method display

} // End of class SolutionVC



/*
 *  Class ValInt
 *   Stores an int-variable whose value can be changed, cf. wrapper 
 *    class Integer.
 */

class ValInt {

    int val;


    public ValInt(int val){
	this.val = val;
    }  // End of ValInt constructor


    public void setVal(int val){
	this.val = val;
    }  // End of method setVal


    public int getVal(){
	return(val);
    }  // End of method getVal

}  // End of class ValInt