package reachability.myreachability;

import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class ReachabilityTag implements Tag {
	public static final String NAME = "REACHABLE_TAG";
	private boolean reachable;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public byte[] getValue() throws AttributeValueException {
		if (reachable)
			return new byte[] {
				(byte) 1
		};
		else
			return new byte[] {
				(byte) 0
			};
	}

}
