package org.jboss.tools.forge.core.internal.runtime;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.forge.core.internal.ForgeCorePlugin;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeType;

public class ForgeEmbeddedRuntime extends ForgeAbstractRuntime {
	
	public static final ForgeRuntime INSTANCE = new ForgeEmbeddedRuntime();
	
	private ForgeEmbeddedRuntime() {}
	
	private String location = null;

	@Override
	public final String getName() {
		return "embedded";
	}
	
	@Override
	public String getLocation() {
		if (location == null) {
			initLocation();
		}
		return location;
	}

	private void initLocation() {
		try {
			File  file = FileLocator.getBundleFile(Platform.getBundle("org.jboss.tools.forge.runtime"));
			for (String str : file.list()) {
				if (str.startsWith("forge-distribution-")) {
					location = file.getCanonicalPath() + File.separator + str;
				}
			}
		} catch (IOException e) {
			ForgeCorePlugin.log(e);
		}
	}
	
	@Override
	public ForgeRuntimeType getType() {
		return ForgeRuntimeType.EMBEDDED;
	}

}
