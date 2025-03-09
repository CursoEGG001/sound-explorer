/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package local.soundexample.sound.explorer.view;

/**
 *
 * @author pc
 */
// imports ommited for brevety.
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.sound.sampled.AudioFormat;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultTreeCellRenderer;

public class SoundView extends JFrame {

    private JComboBox<Object> mixerComboBox;
    private JList<Object> linesList;
    private JTree formatsTree;
    private JButton playButton;
    private JTextArea statusArea;

    public SoundView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Java Sound Explorer");
        setPreferredSize(new Dimension(600, 440)); // Tamaño inicial generoso
        setMinimumSize(new Dimension(512, 340));
        setLayout(new BorderLayout(7, 5)); // Espacio entre componentes
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(4, 4));
        mainPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(2, 4, 3, 4),
                        BorderFactory.createRaisedBevelBorder()
                )
        ); // Margen exterior

        // Panel superior con controles
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLoweredSoftBevelBorder(),
                        BorderFactory.createTitledBorder("Paso a Paso de Configuración.")
                )
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4); // Espacio entre componentes
        gbc.anchor = GridBagConstraints.PAGE_START;

        // Mezcladores
        // Restricciones GridBagLayout Mezcladores
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipady = 35;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Ocupar todo el ancho disponible
        gbc.weightx = 1.0;
        gbc.weighty = 0.2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        mixerComboBox = new JComboBox<>();
        mixerComboBox.setMaximumRowCount(6);
        JScrollPane mixerPane = new JScrollPane(mixerComboBox);
        mixerPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("1. Selección Mezcladores"),
                        BorderFactory.createRaisedSoftBevelBorder()
                )
        );
        controlPanel.add(mixerPane, gbc);

        // Líneas disponibles
        // Restricciones GridBagLayout Líneas
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1; // Ocupar solo una columna
        gbc.weightx = 0.33; // Distribuir espacio equitativamente
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.BOTH; // Expandir vertical y horizontalmente

        linesList = new JList<>();
        JScrollPane linesPane = new JScrollPane(linesList);
        linesPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("2. Obtención Líneas Disponibles"),
                        BorderFactory.createRaisedSoftBevelBorder()
                )
        );
        controlPanel.add(linesPane, gbc);

        // Formatos
        // Restricciones GridBagLayout Formato
        gbc.gridx = 1; // Colocar al lado de las líneas si hay suficiente espacio
        gbc.gridy = 1;
        gbc.weightx = 0.66; // Distribuir espacio equitativamente
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.BOTH;

        formatsTree = new JTree();
        formatsTree.setShowsRootHandles(true); // Mostrar iconos de expansión
        JScrollPane formatsPane = new JScrollPane(formatsTree);
        formatsPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("3. Formatos Soportados"),
                        BorderFactory.createRaisedSoftBevelBorder()
                )
        );
        controlPanel.add(formatsPane, gbc);

        mainPanel.add(controlPanel, BorderLayout.CENTER);

        // Panel inferior con botón y área de estado
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Control proceso"));

        playButton = new JButton("4. Tono de Prueba");
        playButton.setFont(playButton.getFont().deriveFont(14f)); // Tamaño de fuente más grande

        statusArea = new JTextArea(5, 30);
        statusArea.setEditable(false);
        statusArea.setBorder(BorderFactory.createTitledBorder("Estado"));
        statusArea.setFont(statusArea.getFont().deriveFont(12f)); // Tamaño de fuente más legible

        bottomPanel.add(playButton, BorderLayout.LINE_START);
        bottomPanel.add(new JScrollPane(statusArea), BorderLayout.CENTER);

        mainPanel.add(bottomPanel, BorderLayout.PAGE_END);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null); // Centrar la ventana
    }

    public void setMixers(Object[] mixers) {
        mixerComboBox.setModel(new DefaultComboBoxModel<>(mixers));
    }

    public void setLines(List<?> lines) {
        linesList.setListData(lines.toArray());
    }

    public void setFormats(List<AudioFormat> formats) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Supported Formats");

        // Clase contenedora interna para formatear la visualización
        class FormatWrapper {

            private final AudioFormat format;

            public FormatWrapper(AudioFormat format) {
                this.format = format;
            }

            @Override
            public String toString() {
                float frameRate = format.getFrameRate();
                // Sustituir Frame Rate cero con el Sample Rate
                if (frameRate <= 0) {
                    frameRate = format.getSampleRate();
                }

                return String.format("%.1f kHz | %d bits | %d ch | %s | %.1f Hz FR",
                        format.getSampleRate() / 1000.0,
                        format.getSampleSizeInBits(),
                        format.getChannels(),
                        format.getEncoding(),
                        frameRate);
            }

            public AudioFormat getFormat() {
                return format;
            }
        }

        Map<AudioFormat.Encoding, List<AudioFormat>> formatsByEncoding = new LinkedHashMap<>();

        for (AudioFormat format : formats) {
            AudioFormat.Encoding encoding = format.getEncoding();
            formatsByEncoding.computeIfAbsent(encoding, k -> new ArrayList<>()).add(format);
        }

        for (Map.Entry<AudioFormat.Encoding, List<AudioFormat>> entry : formatsByEncoding.entrySet()) {
            DefaultMutableTreeNode encodingNode = new DefaultMutableTreeNode(entry.getKey());

            for (AudioFormat format : entry.getValue()) {
                // Usar el wrapper en lugar del AudioFormat directamente
                DefaultMutableTreeNode formatNode = new DefaultMutableTreeNode(new FormatWrapper(format));
                encodingNode.add(formatNode);
            }

            root.add(encodingNode);
        }

        formatsTree.setModel(new DefaultTreeModel(root));
        expandAllNodes(formatsTree);

        // Personalizar el renderizado para mostrar más detalles al seleccionar
        formatsTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                    boolean expanded, boolean leaf, int row, boolean hasFocus) {

                Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                if (value instanceof DefaultMutableTreeNode node) {
                    Object userObject = node.getUserObject();
                    if (userObject instanceof FormatWrapper wrapper) {
                        AudioFormat format = wrapper.getFormat();
                        float frameRate = format.getFrameRate();
                        if (frameRate <= 0) {
                            frameRate = format.getSampleRate();
                        }
                        String tooltip = String.format("Sample Rate: %.1f kHz \n"
                                + "Bits: %d \n"
                                + "Channels: %d \n"
                                + "Encoding: %s \n"
                                + "Frame Rate: %.1f Hz \n"
                                + "Frame Size: %d bytes \n"
                                + "Big Endian: %b",
                                format.getSampleRate() / 1000.0,
                                format.getSampleSizeInBits(),
                                format.getChannels(),
                                format.getEncoding(),
                                frameRate,
                                format.getFrameSize(),
                                format.isBigEndian()
                        );
                        setToolTipText(tooltip);
                    }
                }
                return c;
            }
        });

    }

// Actualizar el método de obtención del formato seleccionado
    public AudioFormat getSelectedFormat() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) formatsTree.getLastSelectedPathComponent();
        if (selectedNode == null || !selectedNode.isLeaf()) {
            return null;
        }

        Object userObject = selectedNode.getUserObject();
        // Make sure to check for the correct class by type, not just name
        if (userObject != null && userObject.getClass().getName().endsWith("FormatWrapper")) {
            try {
                // Use reflection to safely access the getFormat method
                return (AudioFormat) userObject.getClass().getMethod("getFormat").invoke(userObject);
            } catch (IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                // Handle any reflection exceptions
                return null;
            }
        }
        return null;
    }

    public void setPlayButtonEnabled(boolean validFormat) {
        playButton.setEnabled(validFormat);
    }

// Método auxiliar para expandir todos los nodos
    private void expandAllNodes(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    public void addMixerSelectionListener(ActionListener listener) {
        mixerComboBox.addActionListener(listener);
    }

    public void addLineSelectionListener(ListSelectionListener listener) {
        linesList.addListSelectionListener(listener);
    }

    public void addFormatSelectionListener(TreeSelectionListener listener) {
        formatsTree.addTreeSelectionListener(listener);
    }

    public void addPlayButtonListener(ActionListener listener) {
        playButton.addActionListener(listener);
    }

    public void updateStatus(String message) {
        statusArea.append(message + "\n");
        statusArea.setCaretPosition(statusArea.getDocument().getLength()); // Scroll to bottom
    }

    public Object getSelectedMixer() {
        return mixerComboBox.getSelectedItem();
    }

    public Object getSelectedLine() {
        return linesList.getSelectedValue();
    }

}
