package org.jboss.tools.forge.ui.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.ui.document.ForgeDocument;

public class ForgeHelper {
	
	public static void startForge() {
		final ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
		if (runtime == null || ForgeRuntime.STATE_RUNNING.equals(runtime.getState())) return;
 		ForgeDocument.INSTANCE.connect(runtime);
		Job job = new Job("Starting Forge " + runtime.getVersion()) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				runtime.start(monitor);
				if (runtime.getErrorMessage() != null) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openError(
									null, 
									"Forge Startup Error", 
									runtime.getErrorMessage());
						}			
					});
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
	public static void stopForge() {
		final ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
		if (runtime == null || ForgeRuntime.STATE_NOT_RUNNING.equals(runtime.getState())) return;
		Job job = new Job("Stopping Forge " + runtime.getVersion()) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				runtime.stop(monitor);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
	public static boolean isForgeRunning() {
		ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
		return runtime != null && ForgeRuntime.STATE_RUNNING.equals(runtime.getState());
	}

	public static boolean isForgeStarting() {
		ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
		return runtime != null && ForgeRuntime.STATE_STARTING.equals(runtime.getState());
	}
	
	public static ForgeRuntime getDefaultRuntime() {
		return ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
	}
	
}
