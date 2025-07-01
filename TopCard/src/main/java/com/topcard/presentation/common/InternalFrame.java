package com.topcard.presentation.common;

import javax.swing.*;

/**
 * Utility class for adding JPanel components as JInternalFrames to a JDesktopPane.
 * This class centralizes the logic for creating and positioning internal frames, avoiding code duplication.
 */
public class InternalFrame {

    /**
     * Adds a JPanel as an internal frame to the given JDesktopPane, centered with the specified size.
     *
     * @param desktopPane the JDesktopPane to which the internal frame is added
     * @param title       the title of the internal frame
     * @param panel       the JPanel to be added within the internal frame
     * @param width       the width of the internal frame
     * @param height      the height of the internal frame
     * @param resizable   Allow resize
     */
    public static void addInternalFrame(JDesktopPane desktopPane, String title, JPanel panel, int width, int height, boolean resizable) {
        JInternalFrame internalFrame = new JInternalFrame(title, true, true, true, true);
        internalFrame.add(panel);
        internalFrame.setSize(width, height);
        internalFrame.setVisible(true);
        internalFrame.setResizable(resizable);

        // Center the internal frame within the desktop pane
        internalFrame.setLocation((desktopPane.getWidth() - width) / 2, (desktopPane.getHeight() - height) / 2);

        desktopPane.add(internalFrame);
        desktopPane.moveToFront(internalFrame);
    }
}
