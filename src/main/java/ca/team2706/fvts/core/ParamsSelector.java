package ca.team2706.fvts.core;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class ParamsSelector extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private VisionParams visionParams;
	/**
	 * The content panel
	 */
	private JPanel contentPane;
	
	private MainThread thread;

	public ParamsSelector() throws Exception {
		List<AttributeOptions> options = Utils.getOptions("blobdetect", "networktables",
				"group,prefferedtarget,distance,angleoffset", "crop");
		List<Attribute> attribs = new ArrayList<Attribute>();
		for (AttributeOptions o : options) {
			Attribute a = new Attribute(o.getName(), "");
			attribs.add(a);
		}

		this.visionParams = new VisionParams(attribs, options);

		thread = new MainThread(visionParams);
	}

	private JButton btnUpdate, btnSave;

	/**
	 * Creates a new Parameters Selector
	 * 
	 * @param params The vision parameters tp initialize the parameter selection
	 *               window with
	 */
	public ParamsSelector(VisionParams params, MainThread thread) {
		this.thread = thread;
		this.visionParams = params;

		init();
	}

	private void init() {
		// Makes the program exit when the X button on the window is pressed
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Sets the size of the window
		setBounds(100, 100, 600, 300);
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				setupContent();
			}
		});
		setupContent();
		// Makes the window visible
		setVisible(true);
	}

	private void setupContent() {

		// Initilizes the content panel
		contentPane = new JPanel();
		// Sets the window border
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Sets the layout to a abstract layout
		contentPane.setLayout(null);
		contentPane.setBounds(0, 0, getWidth(), getHeight() * 10);

		int x = 100, y = 100;

		for (Attribute a : visionParams.getAttribs()) {
			if (a.getType() == AttributeOptions.DEFAULT) {
				JTextField field = new JTextField();
				field.setName(a.getName());
				field.setText(a.getValue());
				field.setToolTipText(a.getName());
				field.setBounds(x, y, 100, 40);
				contentPane.add(field);
				x += 120;
				if (x > getWidth() - 150) {
					x = 100;
					y += 60;
				}
			}else if(a.getType() == AttributeOptions.SLIDER) {
				int min = a.getMin() * a.getMultiplier();
				int max = a.getMax() * a.getMultiplier();
				JSlider slider = new JSlider(min,max, (a.getValue().isEmpty() ? min : a.getValueI()*a.getMultiplier()));
				slider.setBounds(x, y, 100, 40);
				slider.setPaintTicks(true);
				slider.setMajorTickSpacing(max / 10);
				slider.setName(a.getName());
				slider.setToolTipText(a.getName());
				contentPane.add(slider);
				x += 120;
				if(x > getWidth() - 150) {
					x = 100;
					y += 60;
				}
			}else if(a.getType() == AttributeOptions.SLIDER_DOUBLE) {
				int min = a.getMin() * a.getMultiplier();
				int max = a.getMax() * a.getMultiplier();
				JSlider slider = new JSlider(min,max,(int) (a.getValue().isEmpty() ? min : a.getValueD()*a.getMultiplier()));
				slider.setBounds(x, y, 100, 40);
				slider.setPaintTicks(true);
				slider.setMajorTickSpacing(max / 10);
				slider.setName(a.getName());
				slider.setToolTipText(a.getName());
				contentPane.add(slider);
				x += 120;
				if(x > getWidth() - 150) {
					x = 100;
					y += 60;
				}
			}
		}
		btnUpdate = new JButton("Apply");
		btnUpdate.setBounds(x, y, 100, 100);
		btnUpdate.addActionListener(this);
		contentPane.add(btnUpdate);
		x += 120;
		if (x > getWidth() - 150) {
			x = 100;
			y += 120;
		}

		btnSave = new JButton("Save");
		btnSave.setBounds(x, y, 100, 100);
		btnSave.addActionListener(this);
		contentPane.add(btnSave);

		// Sets the content pane to the content pane
		setContentPane(contentPane);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnUpdate) {
			thread.stop = true;
			try {
				thread.join();
			} catch (InterruptedException e1) {
				Log.e(e1.getMessage(), true);
			}
			for (Component c : contentPane.getComponents()) {
				if (c instanceof JTextField) {
					// It is a text field
					String name = ((JTextField) c).getName();
					String value = ((JTextField) c).getText();
					
					this.visionParams.putAttrib(new Attribute(name, value));
				}else if(c instanceof JSlider) {
					// Its a slidey boi
					
					JSlider slider = (JSlider) c;
					String name = slider.getName();
					Attribute a = null;
					for(Attribute a1 : visionParams.getAttribs()) {
						if(a1.getName().equals(name)) {
							a = a1;
							break;
						}
					}
					if(a == null) {
						// This shouldn't happen
						Log.e("Oh no, something very bad has happened with the UI that should never ever happen", true);
						return;
					}
					if(a.getType() == AttributeOptions.SLIDER) {
						int value = slider.getValue() / a.getMultiplier();
						String sValue = String.valueOf(value);
						this.visionParams.putAttrib(new Attribute(name, sValue));
					}else if(a.getType() == AttributeOptions.SLIDER_DOUBLE) {
						double value = slider.getValue() / a.getMultiplier();
						String sValue = String.valueOf(value);
						this.visionParams.putAttrib(new Attribute(name, sValue));
					}
				}
			}
			thread = new MainThread(visionParams);
			thread.start();
			setVisible(false);
			dispose();
		} else if (e.getSource() == btnSave) {
			try {
				Utils.saveVisionParams(visionParams);
			} catch (Exception e1) {
				Log.e(e1.getMessage(), true);
				e1.printStackTrace();
			}
		}
	}
}
