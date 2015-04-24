class Heap{

	// This array is where you will store the elements in the heap
	HeapElement storage[];
	
	// This array is where you will store an elements location in the heap
	int location[];

	// Keeps track of the current number of elements in the heap
	int currentSize;

	public Heap(int size){
		storage = new HeapElement[size + 1];
		currentSize = 0;
	}

	public boolean isEmpty(){
		return currentSize == 0;
	}

	public HeapElement removeMin(){
		HeapElement hold = storage[1];//holds the root to return it at the end
		if(currentSize==1)storage[1]=null;//one element case
		else{
			storage[1] = storage[currentSize];//sets the root to the last inserted element
			storage[currentSize] = null;//removes the last inserted element
			bubbleDown(1);//reheapifies if needed
		}
		currentSize--;//updates the current size
		return hold;//returns the removed element
	}

	public void insert(int k){
		HeapElement newElement = new HeapElement(k);
		//first insert case
		if(isEmpty()){
			storage[1] = newElement;
		}else{
			storage[currentSize+1] = newElement;//places in the next availible spot
			bubbleUp(currentSize+1);//reheapifies if needed
		}
		currentSize++;//updates the current size
	}

	/*
	 * Adds new value to the bottom of the heap and
	 * "bubbles up" until it is in the correct position
	 */
	private void bubbleUp(int pos){
		if(parent(pos)==0)return;//to stop if pos is the root
		if(storage[pos].weight-storage[parent(pos)].weight<0){//switches if the parent is larger then its child
			swapElement(pos, parent(pos));
			bubbleUp(parent(pos));//continues until it isn't needed
		}
	}

	/*
	 * Because of a removeMin operation, a value from the bottom
	 * of the heap has been moved to the root.
	 *
	 * "bubble down" until it is in the right position
	 */
	private void bubbleDown(int pos){
		if(!hasRight(pos) && !hasLeft(pos))return;//leaf case
		//has two children case
		if(hasRight(pos) && hasLeft(pos)){
			if(storage[pos].weight-storage[leftChild(pos)].weight>=0 || storage[pos].weight-storage[rightChild(pos)].weight>=0){//enters if one or more children are smaller than the parent
				//checkes which child is smaller and then switches the smaller one with the parent
				if(storage[leftChild(pos)].weight-storage[rightChild(pos)].weight<=0){
					swapElement(pos, leftChild(pos));
					bubbleDown(leftChild(pos));
				}else{
					swapElement(pos, rightChild(pos));
					bubbleDown(rightChild(pos));
				}
			}
		}else if(hasLeft(pos)){//the only hase left child case
			if(storage[pos].weight-storage[leftChild(pos)].weight>0){
				swapElement(pos, leftChild(pos));
				bubbleDown(leftChild(pos));
			}
		}
	}

	private void swapElement(int p1, int p2){
		HeapElement hold = storage[p1];
		storage[p1] = storage[p2];
		storage[p2] = hold;
	}

	//Returns the index of the parent of the node at pos
	private int parent(int pos){
		if(pos == 1)return 0;
		if(pos%2 == 0)return pos/2;
		else return(pos-1)/2;
	}

	//Return the index of the left child of the node at pos
	private int leftChild(int pos){
		return pos*2;
	}

	//Return the index of the right child of the node at pos
	private int rightChild(int pos){
		return pos*2+1;
	}

	private boolean hasLeft(int pos){
		try{
			return storage[leftChild(pos)]!=null;
		}catch(ArrayIndexOutOfBoundsException e){//catches exception if it tries to find a child outside the array
			return false;
		}
	}

	private boolean hasRight(int pos){
		try{
			return storage[rightChild(pos)]!=null;
		}catch(ArrayIndexOutOfBoundsException e){//catches exception if it tries to find a child outside the array
			return false;
		}
	}
}
class HeapElement{
	public int v;//vertex number
	public int weight;//vertex weight
	public HeapElement(int v){
		this.v = v;
		this.weight = v;//Integer.MAX_VALUE;
	}	
}
class HeapTester{
	public static void main(String[] args){
		Heap test = new Heap(10);
		for(int i = 0;i<9;i++){
			test.insert(i);
		}
		for(int i = 8;i>=0;i--){
			System.out.println(test.removeMin().weight);
		}
	}
}
