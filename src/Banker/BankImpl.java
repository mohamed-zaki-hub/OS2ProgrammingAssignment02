package Banker;

/**
 * The Bank
 */
public class BankImpl implements Bank {
    private int n;			
	private int m;			
	private int[] available; 	
	private int[][] maximum; 	
	private int[][] allocation;		
	private int[][] need;		
    boolean safestate = false;
    

    public BankImpl(int[] resources) {
    
    
		m = resources.length;
		n = threadNum;

        available = new int[m];
        System.arraycopy(resources, 0, available, 0, m);

        maximum = new int[threadNum][];
        allocation = new int[threadNum][];
        need = new int[threadNum][];
    }



	@Override
    public void addCustomer(int threadNum, int[] maxDemand) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        maximum[threadNum] = new int[m];
		allocation[threadNum] = new int[m];
		need[threadNum] = new int[m];
		
		System.arraycopy(maxDemand, 0, maximum[threadNum], 0, maxDemand.length);
		System.arraycopy(maxDemand, 0, need[threadNum], 0, maxDemand.length);
    }

    @Override
    public void getState() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.print("Available = \t[");
		for (int i = 0; i < m-1; i++)
			System.out.print(available[i]+" ");
		System.out.println(available[m-1]+"]");
		System.out.print("\nAllocation = \t");
		for (int i = 0; i < n; i++) {
			System.out.print("[");
			for (int j = 0; j < m-1; j++)
				System.out.print(allocation[i][j]+" ");
			System.out.print(allocation[i][m-1]+"]");
		}
		System.out.print("\nMax = \t\t");
		for (int i = 0; i < n; i++) {
			System.out.print("[");
			for (int j = 0; j < m-1; j++)
				System.out.print(maximum[i][j]+" ");
			System.out.print(maximum[i][m-1]+"]");
		}
		System.out.print("\nNeed = \t\t");
		for (int i = 0; i < n; i++) {
			System.out.print("[");
			for (int j = 0; j < m-1; j++)
				System.out.print(need[i][j]+" ");
			System.out.print(need[i][m-1]+"]");
		}
		System.out.println(" ");
		for (int i = 0;i<n;i++)
		{
		for (int j=0;j<1;j++)
			{
			if (need[i][j]<=available[j]&& need[i][j+1]<=available[j+1]&&need[i][j+2]<=available[j+2])
				{
				safestate= true;
				}
			}
		
		}
		if (safestate)
			{
			System.out.println("We are safe,no deadlock :D");
			}
		if (!safestate)
			{
			System.out.println("We are not safe,deadlock waring ");
			}
	    System.out.println(); 
    }

    private boolean isSafeState (int threadNum, int[] request) {
		System.out.print("\n Customer # " + threadNum + " requesting ");
		for (int i = 0; i < m; i++) System.out.print(request[i] + " ");
		
		System.out.print("Available = ");
		for (int i = 0; i < m; i++)
			System.out.print(available[i] + "  ");
		
		for (int i = 0; i < m; i++) 
			if (request[i] > available[i]) {
				System.err.println("INSUFFICIENT RESOURCES");
				return false;
			}
		
		boolean[] canFinish = new boolean[n];
		for (int i = 0; i < n; i++)
			canFinish[i] = false;
		
		int[] avail = new int[m];
		System.arraycopy(available,0,avail,0,available.length);
		
		for (int i = 0; i < m; i++) {
          	avail[i] -= request[i];
			need[threadNum][i] -= request[i];
			allocation[threadNum][i] += request[i];
		}
		
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (!canFinish[j]) {
					boolean temp = true;
					for (int k = 0; k < m; k++) {
						if (need[j][k] > avail[k])
							temp = false;
					}
					if (temp) {
						canFinish[j] = true;
						for (int x = 0; x < m; x++)
							avail[x] += allocation[j][x];
					}
				}	
			}
		}
		
		for (int i = 0; i < m; i++) {
          	need[threadNum][i] += request[i];
          	allocation[threadNum][i] -= request[i];
		}
		
		boolean returnValue = true;
		for (int i = 0; i < n; i++)
			if (!canFinish[i]) {
				returnValue = false;
				break;
			}
		
		return returnValue;
	}
    @Override
    public boolean requestResources(int threadNum, int[] request) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if (!isSafeState(threadNum,request)) {
			return false;
		}
		
		for (int i = 0; i < m; i++) {
			available[i] -= request[i];
			allocation[threadNum][i] += request[i];
			need[threadNum][i] = maximum[threadNum][i] - allocation[threadNum][i];
		}
		
		return true;
	}
    

    @Override
    public void releaseResources(int threadNum, int[] release) {
      System.out.print("\n Customer # " + threadNum + " releasing ");
      for (int i = 0; i < m; i++) System.out.print(release[i] + " ");
      
      for (int i = 0; i < m; i++) {
          available[i] += release[i];
          allocation[threadNum][i] -= release[i];
          need[threadNum][i] = maximum[threadNum][i] + allocation[threadNum][i];
      }
      
      System.out.print("Available = ");
      for (int i = 0; i < m; i++)
            System.out.print(available[i] + "  ");
      
      System.out.print("Allocated = [");
      for (int i = 0; i < m; i++)
          System.out.print(allocation[threadNum][i] + "  "); 
      System.out.print("]"); 
      
  }
}
