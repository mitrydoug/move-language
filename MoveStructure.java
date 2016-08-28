package move;
public interface MoveStructure extends MoveElement{
	public MoveElement[][] getExpansions();
	public void build(MoveElement[] exp);
}