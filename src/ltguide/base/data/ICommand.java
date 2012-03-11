package ltguide.base.data;

public interface ICommand {
	String name();
	
	String permission();
	
	IMessage message();
	
	String syntax();
	
	boolean usesTarget();
}
