package de.osmembrane.controller.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import de.osmembrane.model.ModelProxy;
import de.osmembrane.resources.Resource;
import de.osmembrane.tools.IconLoader.Size;

/**
 * Action to undo the last done edit in the pipeline.
 * 
 * @author tobias_kuhn
 * 
 */
public class UndoAction extends AbstractAction {

	private static final long serialVersionUID = 4603258297595882886L;

	/**
	 * Creates a new {@link UndoAction}
	 */
	public UndoAction() {
		putValue(Action.NAME, "Undo");
		putValue(Action.SMALL_ICON, Resource.PROGRAM_ICON.getImageIcon("undo.png", Size.SMALL));
		putValue(Action.LARGE_ICON_KEY, Resource.PROGRAM_ICON.getImageIcon("undo.png", Size.NORMAL));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ModelProxy.getInstance().accessPipeline().undo();
	}
	
	@Override
	public boolean isEnabled() {
		return ModelProxy.getInstance().accessPipeline().undoAvailable();
	}
}