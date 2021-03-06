/*
 * This file is part of the OSMembrane project.
 * More informations under www.osmembrane.de
 * 
 * The project is licensed under the GNU GENERAL PUBLIC LICENSE 3.0.
 * for more details about the license see http://www.osmembrane.de/license/
 * 
 * Source: $HeadURL$ ($Revision$)
 * Last changed: $Date$
 */

package de.osmembrane.controller.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

import de.osmembrane.Application;
import de.osmembrane.controller.ActionRegistry;
import de.osmembrane.exceptions.ControlledException;
import de.osmembrane.exceptions.ExceptionSeverity;
import de.osmembrane.model.ModelProxy;
import de.osmembrane.model.persistence.FileException;
import de.osmembrane.model.persistence.FileException.Type;
import de.osmembrane.model.persistence.FileType;
import de.osmembrane.model.settings.SettingType;
import de.osmembrane.resources.Resource;
import de.osmembrane.tools.HeadlessSafe;
import de.osmembrane.tools.I18N;
import de.osmembrane.tools.IconLoader.Size;

/**
 * Action to import a pipeline from a file that contains a command line.
 * 
 * @author tobias_kuhn
 * 
 */
public class ImportPipelineAction extends AbstractAction {

    private static final long serialVersionUID = -6853818186320458126L;

    /**
     * Creates a new {@link ImportPipelineAction}
     */
    public ImportPipelineAction() {
        putValue(
                Action.NAME,
                I18N.getInstance().getString(
                        "Controller.Actions.ImportPipeline.Name"));
        putValue(
                Action.SHORT_DESCRIPTION,
                I18N.getInstance().getString(
                        "Controller.Actions.ImportPipeline.Description"));
        putValue(Action.SMALL_ICON, Resource.PROGRAM_ICON.getImageIcon(
                "import_pipeline.png", Size.SMALL));
        putValue(Action.LARGE_ICON_KEY, Resource.PROGRAM_ICON.getImageIcon(
                "import_pipeline.png", Size.NORMAL));
        putValue(
                Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_I,
                        HeadlessSafe.getMenuShortcutKeyMask()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ModelProxy.getInstance().getPipeline().getFunctions().length > 0) {
            ActionRegistry.getInstance().get(NewPipelineAction.class)
                    .actionPerformed(null);
            /* check again */
            if (ModelProxy.getInstance().getPipeline().getFunctions().length > 0) {
                return;
            }
        }

        File startDir = new File((String) ModelProxy.getInstance()
                .getSettings()
                .getValue((SettingType.DEFAULT_WORKING_DIRECTORY)));
        JFileChooser fileChooser = new JFileChooser(startDir);
        fileChooser.setFileFilter(FileType.OSMEMBRANE.getFileFilter());
        fileChooser.addChoosableFileFilter(FileType.BASH.getFileFilter());
        fileChooser.addChoosableFileFilter(FileType.CMD.getFileFilter());
        fileChooser.addChoosableFileFilter(FileType.ALLTYPES.getFileFilter());

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {

            URL file;
            try {
                file = fileChooser.getSelectedFile().toURI().toURL();
            } catch (MalformedURLException e2) {
                file = null;
            }

            try {
                ModelProxy
                        .getInstance()
                        .getPipeline()
                        .importPipeline(
                                file,
                                FileType.fileTypeFor(fileChooser
                                        .getSelectedFile()));

                ActionRegistry.getInstance().get(ViewAllAction.class)
                        .actionPerformed(null);
            } catch (FileException e1) {
                String message;
                if (e1.getType() == Type.SYNTAX_PROBLEM) {
                    message = I18N.getInstance().getString(
                            "Controller.Actions.Load.Failed." + e1.getType(),
                            e1.getParentException().getMessage());
                } else {
                    message = I18N.getInstance().getString(
                            "Controller.Actions.Load.Failed." + e1.getType());
                }

                Application.handleException(new ControlledException(this,
                        ExceptionSeverity.WARNING, e1, message));
            }
        }
    }
}
