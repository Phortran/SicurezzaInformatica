package reachability;

import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class ClassTag implements Tag {
	public static final String TAG_NAME = "clsdflow";

	@Override
	public String getName() {
		return TAG_NAME;
	}

	@Override
	public byte[] getValue() throws AttributeValueException {
		return null;
	}

}
