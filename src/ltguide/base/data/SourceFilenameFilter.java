package ltguide.base.data;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

public class SourceFilenameFilter implements FilenameFilter {
	private final List<String> folders;
	private final List<String> types;
	
	public SourceFilenameFilter(final List<String> folders, final List<String> types) throws IllegalArgumentException {
		if (folders == null && types == null) throw new IllegalArgumentException();
		
		this.folders = folders;
		this.types = types;
	}
	
	@Override
	public boolean accept(final File dir, String name) {
		final String path = dir.getName().toLowerCase();
		name = name.toLowerCase();
		
		for (final String folder : folders)
			if (path.indexOf(folder) == 0) return false;
		
		for (final String type : types)
			if (name.endsWith(type)) return false;
		
		return true;
	}
}
