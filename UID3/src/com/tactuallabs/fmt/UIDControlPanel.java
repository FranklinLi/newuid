package com.tactuallabs.fmt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class UIDControlPanel extends JFrame implements ChangeListener,UIDControlListener, ActionListener {

	private static final long serialVersionUID = -7942213485040433133L;
	private JLabel m_PartitionSliderLabel;
	private JSlider m_PartitionSlider;
	private JLabel m_ShadowSliderLabel;
	private JSlider m_ShadowSlider;
	private JLabel m_TouchPointHighSliderLabel;
	private JSlider m_TouchPointHighSlider;
	private JLabel m_TouchPointLowSliderLabel;
	private JSlider m_TouchPointLowSlider;
	private JLabel m_GroupSliderLabel;
	private JSlider m_GroupSlider;

	private UIDControlListener m_Listener;
	private JButton m_SampleThreshButton;
	
	public UIDControlPanel(UIDControlListener inListener){
		super();
		m_Listener = inListener;
		initUI();
	}
	
	public void initUI(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBackground(Color.green);
		this.getContentPane().add(mainPanel);
	
		// partition thresh .1-10
		// thresh  3200000
		// thresh2 3000000
		// button to sample thresh
		// scale shadows
		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel partitionPanel = new JPanel(new BorderLayout());
		JPanel scaleShadowsPanel = new JPanel(new BorderLayout());
		JPanel scaleGroupsPanel = new JPanel(new BorderLayout());
		JPanel threshPanel = new JPanel(new BorderLayout());
		JPanel highLowThreshPanel = new JPanel();
		highLowThreshPanel.setLayout(new BoxLayout(highLowThreshPanel, BoxLayout.Y_AXIS));
		JPanel highThreshPanel = new JPanel(new BorderLayout());
		JPanel lowThreshPanel = new JPanel(new BorderLayout());
		highLowThreshPanel.add(highThreshPanel);
		highLowThreshPanel.add(lowThreshPanel);
		m_SampleThreshButton = new JButton("Sample");
		threshPanel.add(highLowThreshPanel,BorderLayout.CENTER);
		threshPanel.add(m_SampleThreshButton,BorderLayout.EAST);
		mainPanel.add(partitionPanel);
		mainPanel.add(scaleShadowsPanel);
		mainPanel.add(scaleGroupsPanel);
		mainPanel.add(threshPanel);
		
		m_SampleThreshButton.addActionListener(this);
		
		Font font = new Font("San Serif", Font.PLAIN, 32);
		m_SampleThreshButton.setFont(font);
		
		// partition slider
		m_PartitionSliderLabel = new JLabel("Partition Threshold: 2.7", JLabel.CENTER);
		m_PartitionSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		m_PartitionSliderLabel.setFont(font);
		m_PartitionSlider = new JSlider(JSlider.HORIZONTAL,
                                              1, 100, 27);
        m_PartitionSlider.addChangeListener(this);
        m_PartitionSlider.setPaintTicks(false);
        m_PartitionSlider.setPaintLabels(false);
        m_PartitionSlider.setBorder(
                BorderFactory.createEmptyBorder(0,0,10,0));
        partitionPanel.add(m_PartitionSliderLabel,BorderLayout.NORTH);
        partitionPanel.add(m_PartitionSlider,BorderLayout.CENTER);
        partitionPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));


		// scaleShadows slider
		m_ShadowSliderLabel = new JLabel("Shadow Scalar: 3.0", JLabel.CENTER);
		m_ShadowSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		m_ShadowSliderLabel.setFont(font);
		m_ShadowSlider = new JSlider(JSlider.HORIZONTAL,
                                              1, 100, 30);
        m_ShadowSlider.addChangeListener(this);
        m_ShadowSlider.setPaintTicks(false);
        m_ShadowSlider.setPaintLabels(false);
        m_ShadowSlider.setBorder(
                BorderFactory.createEmptyBorder(0,0,10,0));
        scaleShadowsPanel.add(m_ShadowSliderLabel,BorderLayout.NORTH);
        scaleShadowsPanel.add(m_ShadowSlider,BorderLayout.CENTER);
        scaleShadowsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        
     // scaleGroups slider
     		m_GroupSliderLabel = new JLabel("Group Scalar: 0.5", JLabel.CENTER);
     		m_GroupSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
     		m_GroupSliderLabel.setFont(font); 
     		m_GroupSlider = new JSlider(JSlider.HORIZONTAL,
                                                   1, 100, 5);
             m_GroupSlider.addChangeListener(this);
             m_GroupSlider.setPaintTicks(false);
             m_GroupSlider.setPaintLabels(false);
             m_GroupSlider.setBorder(
                     BorderFactory.createEmptyBorder(0,0,10,0));
             scaleGroupsPanel.add(m_GroupSliderLabel,BorderLayout.NORTH);
             scaleGroupsPanel.add(m_GroupSlider,BorderLayout.CENTER);
             scaleGroupsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
     // highThresh slider
      		m_TouchPointHighSliderLabel = new JLabel("Touch Thresh 1: 3200000", JLabel.LEFT);
      		m_TouchPointHighSliderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
      		m_TouchPointHighSliderLabel.setBorder(
                     BorderFactory.createEmptyBorder(0,0,10,0));
      		m_TouchPointHighSliderLabel.setFont(font);  
      		m_TouchPointHighSlider = new JSlider(JSlider.HORIZONTAL,
                                                    500000, 40000000, 3200000);
              m_TouchPointHighSlider.addChangeListener(this);
              m_TouchPointHighSlider.setPaintTicks(false);
              m_TouchPointHighSlider.setPaintLabels(false);
              m_TouchPointHighSlider.setBorder(
                      BorderFactory.createEmptyBorder(0,0,10,0));
              highThreshPanel.add(m_TouchPointHighSliderLabel,BorderLayout.NORTH);
              highThreshPanel.add(m_TouchPointHighSlider,BorderLayout.CENTER);
              highThreshPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
              
              
           // LowThresh slider
       		m_TouchPointLowSliderLabel = new JLabel("Touch Thresh 2: 3000000", JLabel.LEFT);
       		m_TouchPointLowSliderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
       		m_TouchPointLowSliderLabel.setBorder(
                      BorderFactory.createEmptyBorder(0,0,10,0));
       		m_TouchPointLowSliderLabel.setFont(font);
               m_TouchPointLowSlider = new JSlider(JSlider.HORIZONTAL,
                                                     500000, 40000000, 3000000);
               m_TouchPointLowSlider.addChangeListener(this);
               m_TouchPointLowSlider.setPaintTicks(false);
               m_TouchPointLowSlider.setPaintLabels(false);
               m_TouchPointLowSlider.setBorder(
                       BorderFactory.createEmptyBorder(0,0,10,0));
               lowThreshPanel.add(m_TouchPointLowSliderLabel,BorderLayout.NORTH);
               lowThreshPanel.add(m_TouchPointLowSlider,BorderLayout.CENTER);
               lowThreshPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	}
	
	public static void main(String[] args) {

		UIDControlListener listener = new UIDControlListener(){

			@Override
			public void onPartitionUpdate(double in) {
				System.out.println("Partition! " + in);
			}

			@Override
			public void onShadowScalarUpdate(double in) {
				System.out.println("Shadow! " + in);
			}
			
			@Override
			public void onSameGroupLastFrameScalarUpdate(double in){
				System.out.println("Same Group! "+in);
			}

			@Override
			public void onThresh1Update(int in) {
				System.out.println("Thresh1! " + in);
			}

			@Override
			public void onThresh2Update(int in) {
				System.out.println("Thresh2! " + in);			
			}

			@Override
			public void onSampleThreshPress() {
				System.out.println("Sample!");
			}};
		
		UIDControlPanel panel = new UIDControlPanel(listener);
		panel.setBounds(100, 100, 200, 200);
		panel.pack();
		panel.setVisible(true);
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		if(arg0.getSource() == m_PartitionSlider){
			m_PartitionSliderLabel.setText("Partition Threshold: " + m_PartitionSlider.getValue()/10d);
			m_Listener.onPartitionUpdate(m_PartitionSlider.getValue()/10d);
		}else if(arg0.getSource() == m_ShadowSlider){
			m_ShadowSliderLabel.setText("Shadow Scalar: " + m_ShadowSlider.getValue()/10d);
			m_Listener.onShadowScalarUpdate(m_ShadowSlider.getValue()/10d);
		}else if(arg0.getSource() == m_TouchPointHighSlider){
			m_TouchPointHighSliderLabel.setText("Touch Thresh 1: " + m_TouchPointHighSlider.getValue());
			m_Listener.onThresh1Update(m_TouchPointHighSlider.getValue());
		}else if(arg0.getSource() == m_TouchPointLowSlider){
			m_TouchPointLowSliderLabel.setText("Touch Thresh 2: " + m_TouchPointLowSlider.getValue());
			m_Listener.onThresh2Update(m_TouchPointLowSlider.getValue());
		}else if(arg0.getSource() == m_GroupSlider){
			m_GroupSliderLabel.setText("Group Scalar: " + m_GroupSlider.getValue()/10d);
			m_Listener.onSameGroupLastFrameScalarUpdate(m_GroupSlider.getValue()/10d);
		}
		
	}

	@Override
	public void onPartitionUpdate(double in) {
		this.m_PartitionSlider.setValue((int)(in*10));
	}

	@Override
	public void onShadowScalarUpdate(double in) {
		this.m_ShadowSlider.setValue((int)(in*10));
	}

	@Override
	public void onThresh1Update(int in) {
		this.m_TouchPointHighSlider.setValue(in);
	}

	@Override
	public void onThresh2Update(int in) {
		this.m_TouchPointLowSlider.setValue(in);		
	}

	@Override
	public void onSampleThreshPress() {
		// do nothing
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == m_SampleThreshButton){
			m_Listener.onSampleThreshPress();
		}
		
	}

	@Override
	public void onSameGroupLastFrameScalarUpdate(double in) {
		// TODO Auto-generated method stub
		m_GroupSlider.setValue((int)(in*10));
	}

}
