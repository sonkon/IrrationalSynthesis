import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.awt.Event;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.Timer;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
public class JavaSynth {
    static final int SAMPLE_RATE = 16 * 1024;

    public static void main(String[] args) throws LineUnavailableException {
        //final AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
        //SourceDataLine line = AudioSystem.getSourceDataLine(af);
        //line.open(af, SAMPLE_RATE);
        //line.start();
        final AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);//Used to configure the how fast the sample rate will be
        SourceDataLine line = AudioSystem.getSourceDataLine(af);//Used to send the frequency and hertz data to the speakers
        //AudioInputStream inputStream = new AudioInputStream(line);
        line.open(af, SAMPLE_RATE);//Readies the line for sending data to the speakers
        line.start();//Ready for data to be turned into sound
        //boolean forwardNotBack = true;

        JSlider release = new JSlider(5,2000);//Slider for how long the note is supposed to be held for after you press it
        JSlider volumeSlider = new JSlider(-30,6);//Slider for how loud the synth plays at
        release.setName("Release Setting");
        volumeSlider.setName("Volume");
        JPanel panel = new JPanel(new BorderLayout());//Sets the panel for the synth to be put into
        panel.setLayout(new FlowLayout());//Sets the layout for the panel
        final int frameSize = 500;//Creates a variable that will be used for the frameSize
        JFrame frame = new JFrame("Synth");//Creates a new JFrame called frame
        frame.setSize(frameSize, frameSize);//sets the size of the frame with frameSize
        JFrame frame2 = new JFrame("GUI");//Creates a new JFrame called frame
        frame2.setSize(frameSize, frameSize);//sets the size of the frame with frameSize

        JRadioButton oct1Button = new JRadioButton("Octave 1");//Octave buttons to switch octaves

        JRadioButton oct2Button = new JRadioButton("Octave 2");

        JRadioButton oct3Button = new JRadioButton("Octave 3");

        JRadioButton oct4Button = new JRadioButton("Octave 4");

        JRadioButton sineWaveButton = new JRadioButton("Sine Wave");//These will choose which type of wave is being played

        JRadioButton squareWaveButton = new JRadioButton("Square Wave");

        JRadioButton fluteWaveButton = new JRadioButton("Flute Wave");

        JRadioButton noiseButton = new JRadioButton("Psuedo-Noise Wave");

        JRadioButton distortSineButton = new JRadioButton("Sine/FM Wave");

        JRadioButton fmButton = new JRadioButton("FM Wave");

        JRadioButton echoSquareButton = new JRadioButton("Echo Square Wave");
        JLabel infoForKeys = new JLabel("White Keys:A,S,D,F,G,H,J,K,L Black Keys:W,E,T,Y,U,O,P");
        JLabel infoForWaves1 = new JLabel("");
        JLabel infoForWaves2 = new JLabel("");
        JLabel infoForWaves3 = new JLabel("");
        JLabel infoForWaves4 = new JLabel("");
        JLabel infoForWaves5 = new JLabel("");
        JLabel infoForWaves6 = new JLabel("");
        JLabel infoForWaves7 = new JLabel("");
        ButtonGroup octaveSwitch = new ButtonGroup();//Groups the buttons to their respective types
        ButtonGroup waveSwitch = new ButtonGroup();

        octaveSwitch.add(oct1Button);//Adds the buttons to their respective group
        octaveSwitch.add(oct2Button);
        octaveSwitch.add(oct3Button);
        octaveSwitch.add(oct4Button);
        waveSwitch.add(sineWaveButton);
        waveSwitch.add(squareWaveButton);
        waveSwitch.add(fluteWaveButton);
        waveSwitch.add(noiseButton);
        waveSwitch.add(distortSineButton);
        waveSwitch.add(fmButton);
        waveSwitch.add(echoSquareButton);

        FloatControl volume= (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);//Used to control the volume of the synth
        volume.setValue(-30.0f);

        class Synthesizer extends JComponent implements ActionListener
        {
            // final int SAMPLE_RATE = 96000;
            // AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
            // final SourceDataLine line = AudioSystem.getSourceDataLine(af);
            double note = 0.0;//Variable that will be used to store the frequency of the note being played
            double attack = 1;//Supposed to be the amount of time it will take to reach a certain volume but ended up being used to bring the fm sound in the sine/fm wave
            public byte[] createSinWaveBuffer(double freq, int ms)//Used to create the sine wave
            {
                int samples = (int)((ms * SAMPLE_RATE) / 1000);//Used to figure how many samples of the wave will be playing in the alloted milliseconds
                byte[] output = new byte[samples];//The byte array that will hold all the data to be sent to the speakers as frequency data
                //
                double period = (double)SAMPLE_RATE / freq;//The period of the wave
                for (int i = 0; i < output.length; i++) {
                    //For i which will be the length of the outputted waveform, the loop renders the wave for each part of the sample, and is continually sending this data to the SourcDataLine to then be sent to the speakers 
                    double angle = 2.0 * Math.PI * i / period;//The angle at which the waveform is currently at
                    output[i] = (byte)(Math.sin(angle) * 127f);//Outputs the sine of the angle and 127f is the time
                }

                return output;//Returns the output array after the for loop because the waveform has now been finished being created
            }

            public byte[] createDistortionWaveBuffer(double freq, int ms,double att)//Used to create a distorted sine wave
            {
                int samples = (int)((ms * SAMPLE_RATE) / 1000);//Used to figure how many samples of the wave will be playing in the alloted milliseconds
                byte[] output = new byte[samples];//The byte array that will hold all the data to be sent to the speakers as frequency data
                double period = (double)SAMPLE_RATE / freq;//The period of the wave
                for (int i = 0; i < output.length; i++) {
                    //For i which will be the length of the outputted waveform, the loop renders the wave for each part of the sample, and is continually sending this data to the SourcDataLine to then be sent to the speakers 
                    att*=1.00001;//Will "slowly" increase the amplitude of the waveform until it becomes distorted
                    double angle = 2.0 * Math.PI * i / period;//The angle at which the waveform is currently at
                    output[i] = (byte)((att*Math.sin(angle) * 127f));//Outputs the sine of the angle while being multiplied by att to distort the wave over time 
                }

                return output;//Returns the output array after the for loop because the waveform has now been finished being created
            }

            public byte[] createFM1WaveBuffer(double freq, int ms)//Used to create a frequency-modulated (FM) wave
            {
                int samples = (int)((ms * SAMPLE_RATE) / 1000);//Used to figure how many samples of the wave will be playing in the alloted milliseconds
                byte[] output = new byte[samples];//The byte array that will hold all the data to be sent to the speakers as frequency data
                double freqFM = (freq +((4*freq)-freq)+((2*freq)-freq))/output.length;//Creates a copy of the frequency that the user played and modulates it by adding the original frequency by a multiple of 4 and 2
                //
                //This concept of using multiples of 2's is integral to frequency modulation because without a 1:2 ratio, the waveform will then contains inharmonics, which sound unpleasing to the ear
                //
                double period = (double)SAMPLE_RATE / freq;//The period of the wave
                double period2 = (double)SAMPLE_RATE / freqFM;//The period of the frequency-modulated wave
                for (int i = 0; i < output.length; i++) {
                    //For i which will be the length of the outputted waveform, the loop renders the wave for each part of the sample, and is continually sending this data to the SourcDataLine to then be sent to the speakers 
                    double angle = 2.0 * Math.PI * i / period;//The angle at which the waveform is currently at
                    double angle2 = 2.0 * Math.PI * i/period2;//The angle at which the frequency-modulated waveform is currently at
                    output[i] = (byte) ((Math.sin(angle) * 127f)+(Math.sin(angle2) * 127f));//Outputs the sine of the angle as well as the sine of the frequency-modulated waveform
                    //This technique is also called phase-modulation in the digital realm of synthesis
                }

                return output;//Returns the output array after the for loop because the waveform has now been finished being created
            }

            public byte[] createFluteWaveBuffer(double freq, int ms)//Used to create a psuedo-flute sounding wave
            {
                int samples = (int)((ms * SAMPLE_RATE) / 1000);//Used to figure how many samples of the wave will be playing in the alloted milliseconds
                byte[] output = new byte[samples];//The byte array that will hold all the data to be sent to the speakers as frequency data
                double period = (double)SAMPLE_RATE / freq;//The period of the wave
                for (int i = 0; i < output.length; i++) {
                    //For i which will be the length of the outputted waveform, the loop renders the wave for each part of the sample, and is continually sending this data to the SourcDataLine to then be sent to the speakers 
                    double angle = 2.0 * Math.PI * (2.0*i + 1.0) / period;//The angle at which the waveform is currently at
                    output[i] = (byte) ((450*Math.sin(angle)*127f)/i);//Output of the sine of the angle which is then multiplied by 450, then the whole thing is divided by the length of the ouput, i
                }
                //
                //This was originally supposed to be a triangle waveform, but a happy accident created a psuedo-flute sound
                //
                return output;//Returns the output array after the for loop because the waveform has now been finished being created
            }

            public byte[] createGlitchWaveBuffer(double freq, int ms)//Used to create a non-random noise wave
            {
                int samples = (int)((ms * SAMPLE_RATE) / 1000);//Used to figure how many samples of the wave will be playing in the alloted milliseconds
                byte[] output = new byte[samples];//The byte array that will hold all the data to be sent to the speakers as frequency data
                double period = (double)SAMPLE_RATE / freq;//The period of the wave
                for (int i = 0; i < output.length; i++) {
                    //For i which will be the length of the outputted waveform, the loop renders the wave for each part of the sample, and is continually sending this data to the SourcDataLine to then be sent to the speakers 
                    double angle = 2.0 * Math.PI * (2.0*i + 1.0) / period;//The angle at which the waveform is currently at
                    output[i] = (byte) (450*Math.sin(angle)*127f);//Output of the sine of the angle which is then multiplied by 450 to increase the amplitude of the waveform
                }
                //
                //This was originally supposed to be a triangle waveform, but a happy accident created a glitched-out noise sound
                //
                return output;//Returns the output array after the for loop because the waveform has now been finished being created
            }

            public byte[] createSquareWaveBuffer(double freq, int ms)//Used to create a square wave
            {
                int samples = (int)((ms * SAMPLE_RATE) / 1000);//Used to figure how many samples of the wave will be playing in the alloted milliseconds
                byte[] output = new byte[samples];//The byte array that will hold all the data to be sent to the speakers as frequency data
                double period = (double)SAMPLE_RATE / freq;//The period of the wave
                for (int i = 0; i < output.length; i++) {
                    //For i which will be the length of the outputted waveform, the loop renders the wave for each part of the sample, and is continually sending this data to the SourcDataLine to then be sent to the speakers 
                    double angle = 2.0 * Math.PI * (2.0*i - 1.0) / period;//The angle at which the waveform is currently at
                    output[i] = (byte)(10.0*Math.signum(Math.sin(angle)));//Outputs a 10, 0 or a -10 because of the sign function
                }
                //
                //For a perfect square wave, the wave must jump from 1 to -1 instantly, but since we don't have an infinite sample rate, there is a rise and falling action
                //The signum() function returns a 1 if the explicit parameter is bigger than 0, and a -1 if the explicit parameter is less than 0
                //The whole value is then multiplied by 10 to increase the amplitude and volume of the waveform because a 1 and -1 isn't loud enough
                //
                return output;//Returns the output array after the for loop because the waveform has now been finished being created
            }

            public byte[] createDipWaveBuffer(double freq, int ms)//Used to create an "echo-sounding" square wave
            {
                int samples = (int)((ms * SAMPLE_RATE) / 1000);//Used to figure how many samples of the wave will be playing in the alloted milliseconds
                byte[] output = new byte[samples];//The byte array that will hold all the data to be sent to the speakers as frequency data
                double increment = 0.01;//Will be used to increment the value of the output
                double period = (double)SAMPLE_RATE / freq;//The period of the wave
                for (int i = 0; i < output.length; i++) {
                    //For i which will be the length of the outputted waveform, the loop renders the wave for each part of the sample, and is continually sending this data to the SourcDataLine to then be sent to the speakers 
                    double angle = 2.0 * Math.PI * (2.0*i - 1.0) / period;//The angle at which the waveform is currently at
                    output[i] = (byte)((increment*10.0*Math.signum(Math.sin(angle))));//Same as the square wave but is being added by an increasing value over time
                    increment+=0.01;//Adds 0.01 to the variable increment
                }
                increment = 0.01;//Resets the value back to 0.01 for the next call of the DipWave
                //
                //Was originally an experiment to see the effect of an increasing amplitude over time
                //My original thought was the volume would increase over time, but instead it gave the effect of a LFO (Low-frequency oscillator) modulated square wave
                //
                return output;//Returns the output array after the for loop because the waveform has now been finished being created
            }

            public void Synth(SourceDataLine line, double note, int typeOfWave)//Used to play notes in various timbres (waveforms)
            {
                boolean forwardNotBack = true;//Will be used to stop the playing of the wave so it doesn't drone
                if(typeOfWave == 1)//Sine Wave
                {
                    for(double freq = note; freq >= note;)  {//For the frequency equalling note, the following code is performed
                        byte [] toneBuffer = createSinWaveBuffer(freq,release.getValue());//Creates the sine wave to be sent to the speakers
                        line.write(toneBuffer,0,toneBuffer.length);//Writes the sine wave data to a DataSourceLine to be sent to the speakers
                        if(forwardNotBack)  {   
                            freq -= 5;  
                            forwardNotBack = false;  }
                        else  {
                            freq -= 5;
                            forwardNotBack = true;  

                        } 

                    }
                    //attack=1;
                }
                if(typeOfWave == 2)//Square Wave
                {
                    for(double freq = note; freq >= note;)  {
                        byte [] toneBuffer = createSquareWaveBuffer(freq, release.getValue());
                        line.write(toneBuffer, 0, toneBuffer.length);
                        if(forwardNotBack)  {
                            freq -= 5;  
                            forwardNotBack = false;  }
                        else  {
                            freq -= 5;
                            forwardNotBack = true;  
                        }  
                    }
                }
                if(typeOfWave == 3)//Flute Wave
                {
                    for(double freq = note; freq >=note;)
                    {
                        byte [] toneBuffer = createFluteWaveBuffer(freq, release.getValue());
                        line.write(toneBuffer, 0, toneBuffer.length);
                        if(forwardNotBack)
                        {
                            freq -= 5;
                            forwardNotBack = false;
                        }
                        else
                        {
                            freq -= 5;
                            forwardNotBack = true;
                        }
                    }
                }
                if(typeOfWave == 4)//Noise Wave
                {
                    for(double freq =note; freq >= note;)  {
                        byte [] toneBuffer = createGlitchWaveBuffer(freq, release.getValue());
                        line.write(toneBuffer, 0, toneBuffer.length);
                        if(forwardNotBack)  {
                            freq -= 5;  
                            forwardNotBack = false;  }
                        else  {
                            freq -= 5;
                            forwardNotBack = true;  
                        }  
                    }
                }
                if(typeOfWave == 5)//Distorted-Sine Wave
                {
                    for(double freq =note; freq >= note;)  {
                        byte [] toneBuffer = createDistortionWaveBuffer(freq,release.getValue(),attack);
                        line.write(toneBuffer, 0, toneBuffer.length);
                        if(forwardNotBack)  {
                            freq -= 5;  
                            forwardNotBack = false;  }
                        else  {
                            freq -= 5;
                            forwardNotBack = true;  
                        }  
                    }
                }
                if(typeOfWave == 6)//FM Wave
                {
                    for(double freq =note; freq >= note;)  {
                        byte [] toneBuffer = createFM1WaveBuffer(freq, release.getValue());
                        line.write(toneBuffer, 0, toneBuffer.length);
                        if(forwardNotBack)  {
                            freq -= 5;  
                            forwardNotBack = false;  }
                        else  {
                            freq -= 5;
                            forwardNotBack = true;  
                        }  
                    }
                }
                if(typeOfWave == 7)//Echo Square Wave
                {
                    for(double freq =note; freq >= note;)  {
                        byte [] toneBuffer = createDipWaveBuffer(freq, release.getValue());
                        line.write(toneBuffer, 0, toneBuffer.length);
                        if(forwardNotBack)  {
                            freq -= 5;  
                            forwardNotBack = false;  }
                        else  {
                            freq -= 5;
                            forwardNotBack = true;  
                        }  
                    }
                }
            }

            public void setNote(double noteValue)//Used to set the value of note
            {
                note = noteValue;
            }

            public double getNote()//Used to return the value of note
            {
                return note;
            }

            public void actionPerformed(ActionEvent e)//Every 5 milliseconds, the code below is performed
            {   
                frame.toFront();//The frame is being called to the front so it is always in focus
                frame.setState(frame.NORMAL);//Sets the state of the frame to normal
                volume.setValue(volumeSlider.getValue());//Sets the value of the volume from the volumeSlider
            }
        }

        final Synthesizer synth = new Synthesizer();//Creates an instance of Synthesizer

        frame.add(synth);//Adds the synth to the frame

        Timer clock = new Timer(5,synth);//creates a timer which will continously keep the synth frame in focus

        frame.setVisible(true);//Now you can use the synth

        class MIDI implements KeyListener//Will listen to KeyEvents to trigger the synth 
        {
            int choice = 1;//Choice of the type of synth
            int octave = 1;//Octave choice
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_V || (oct1Button.isSelected()==true))//If V is pressed or the octave 1 button is pressed, octave 0 will now be used
                {
                    octave = 0;
                }
                if(e.getKeyCode() == KeyEvent.VK_B || (oct2Button.isSelected()==true))
                {
                    octave = 1;
                }
                if(e.getKeyCode() == KeyEvent.VK_N || (oct3Button.isSelected()==true))
                {
                    octave = 2;
                }
                if(e.getKeyCode() == KeyEvent.VK_M || (oct4Button.isSelected()==true))
                {
                    octave = 3;
                }
                if(e.getKeyCode() == KeyEvent.VK_A)//If A is pressed, the note will be set depending on the octave that is chosen
                {
                    if(octave == 0)
                    {
                        synth.setNote(65.406);
                    }
                    else if(octave == 1)
                    {
                        synth.setNote(130.81);
                    }
                    else if(octave == 2)
                    {
                        synth.setNote(261.63);
                    }
                    else
                    {
                        synth.setNote(523.25);                    
                    }
                }
                else if(e.getKeyCode() == KeyEvent.VK_W)//If W is pressed, the note will be set depending on the octave that is chosen
                {
                    if(octave == 0)
                    {
                        synth.setNote(69.296);
                    }
                    else if(octave == 1)
                    {
                        synth.setNote(138.59);
                    }
                    else if(octave == 2)
                    {
                        synth.setNote(277.18);
                    }
                    else
                    {
                        synth.setNote(554.37);                    
                    }

                }
                else if(e.getKeyCode() == KeyEvent.VK_S)//If S is pressed, the note will be set depending on the octave that is chosen
                {
                    if(octave == 0)
                    {
                        synth.setNote(73.416);
                    }
                    else if(octave == 1)
                    {
                        synth.setNote(146.83);
                    }
                    else if(octave == 2)
                    {
                        synth.setNote(293.67);
                    }
                    else
                    {
                        synth.setNote(587.33);                    
                    }

                }
                else if(e.getKeyCode() == KeyEvent.VK_E)//If E is pressed, the note will be set depending on the octave that is chosen
                {
                    if(octave == 0)
                    {
                        synth.setNote(77.782);
                    }
                    else if(octave == 1)
                    {
                        synth.setNote(155.56);
                    }
                    else if(octave == 2)
                    {
                        synth.setNote(311.13);
                    }
                    else
                    {
                        synth.setNote(622.25);                    
                    }
                    //synth.setNote(311.13);
                }
                else if(e.getKeyCode() == KeyEvent.VK_D)//If D is pressed, the note will be set depending on the octave that is chosen
                {
                    if(octave == 0)
                    {
                        synth.setNote(82.407);
                    }
                    else if(octave == 1)
                    {
                        synth.setNote(164.81);
                    }
                    else if(octave == 2)
                    {
                        synth.setNote(329.63);
                    }
                    else
                    {
                        synth.setNote(659.26);                    
                    }
                    //synth.setNote(329.63);
                }
                else if(e.getKeyCode() == KeyEvent.VK_F)//If F is pressed, the note will be set depending on the octave that is chosen
                {
                    if(octave == 0)
                    {
                        synth.setNote(87.307);
                    }
                    else if(octave == 1)
                    {
                        synth.setNote(174.61);
                    }
                    else if(octave == 2)
                    {
                        synth.setNote(349.23);
                    }
                    else
                    {
                        synth.setNote(698.46);                    
                    }
                    //synth.setNote(349.23);
                }
                else if(e.getKeyCode() == KeyEvent.VK_T)//If T is pressed, the note will be set depending on the octave that is chosen
                {
                    if(octave == 0)
                    {
                        synth.setNote(92.499);
                    }
                    else if(octave == 1)
                    {
                        synth.setNote(185.00);
                    }
                    else if(octave == 2)
                    {
                        synth.setNote(369.99);
                    }
                    else
                    {
                        synth.setNote(739.99);                    
                    }
                    //synth.setNote(369.99);
                }
                else if(e.getKeyCode() == KeyEvent.VK_G)//If G is pressed, the note will be set depending on the octave that is chosen
                {
                    if(octave == 0)
                    {
                        synth.setNote(97.999);
                    }
                    else if(octave == 0)
                    {
                        synth.setNote(97.999);
                    }
                    else if(octave == 1)
                    {
                        synth.setNote(196.00);
                    }
                    else if(octave == 2)
                    {
                        synth.setNote(392.00);
                    }
                    else
                    {
                        synth.setNote(783.99);                    
                    }
                    //synth.setNote(392.00);
                }
                else if(e.getKeyCode() == KeyEvent.VK_Y)//If Y is pressed, the note will be set depending on the octave that is chosen
                {
                    if(octave == 0)
                    {
                        synth.setNote(103.83);
                    }
                    else if(octave == 1)
                    {
                        synth.setNote(207.65);
                    }
                    else if(octave == 2)
                    {
                        synth.setNote(415.30);
                    }
                    else
                    {
                        synth.setNote(830.61);                    
                    }
                    //synth.setNote(415.30);
                }
                else if(e.getKeyCode() == KeyEvent.VK_H)//If H is pressed, the note will be set depending on the octave that is chosen
                {
                    if(octave == 0)
                    {
                        synth.setNote(110.00);
                    }
                    else if(octave == 1)
                    {
                        synth.setNote(220.00);
                    }
                    else if(octave == 2)
                    {
                        synth.setNote(440.00);
                    }
                    else
                    {
                        synth.setNote(880.00);                    
                    }
                    //synth.setNote(440.00);
                }
                else if(e.getKeyCode() == KeyEvent.VK_U)//If U is pressed, the note will be set depending on the octave that is chosen
                {
                    if(octave == 0)
                    {
                        synth.setNote(116.54);
                    }
                    else if(octave == 1)
                    {
                        synth.setNote(233.08);
                    }
                    else if(octave == 2)
                    {
                        synth.setNote(466.16);
                    }
                    else
                    {
                        synth.setNote(932.33);                    
                    }
                    //synth.setNote(466.16);
                }
                else if(e.getKeyCode() == KeyEvent.VK_J)//If J is pressed, the note will be set depending on the octave that is chosen
                {
                    if(octave == 0)
                    {
                        synth.setNote(123.47);
                    }
                    else if(octave == 1)
                    {
                        synth.setNote(246.94);
                    }
                    else if(octave == 2)
                    {
                        synth.setNote(493.88);
                    }
                    else
                    {
                        synth.setNote(987.77);                    
                    }
                    //synth.setNote(493.88);
                }
                else if(e.getKeyCode() == KeyEvent.VK_K)//If K is pressed, the note will be set depending on the octave that is chosen
                {
                    if(octave == 0)
                    {
                        synth.setNote(130.81);
                    }
                    else if(octave == 1)
                    {
                        synth.setNote(261.63);
                    }
                    else if(octave == 2)
                    {
                        synth.setNote(523.25);
                    }
                    else
                    {
                        synth.setNote(1046.5);                    
                    }
                    //synth.setNote(523.25);
                }
                else if(e.getKeyCode() == KeyEvent.VK_O)//If O is pressed, the note will be set depending on the octave that is chosen
                {
                    if(octave == 0)
                    {
                        synth.setNote(138.59);
                    }
                    else if(octave == 1)
                    {
                        synth.setNote(277.18);
                    }
                    else if(octave == 2)
                    {
                        synth.setNote(554.37);
                    }
                    else
                    {
                        synth.setNote(1108.7);                    
                    }
                    //synth.setNote(554.37);
                }
                else if(e.getKeyCode() == KeyEvent.VK_L)//If L is pressed, the note will be set depending on the octave that is chosen
                {
                    if(octave == 0)
                    {
                        synth.setNote(146.83);
                    }
                    else if(octave == 1)
                    {
                        synth.setNote(293.67);
                    }
                    else if(octave == 2)
                    {
                        synth.setNote(587.33);
                    }
                    else
                    {
                        synth.setNote(1174.7);                    
                    }
                    //synth.setNote(587.33);
                }
                else if(e.getKeyCode() == KeyEvent.VK_P)//If P is pressed, the note will be set depending on the octave that is chosen
                {
                    if(octave == 0)
                    {
                        synth.setNote(155.56);
                    }
                    else if(octave == 1)
                    {
                        synth.setNote(311.13);
                    }
                    else if(octave == 2)
                    {
                        synth.setNote(622.25);
                    }
                    else
                    {
                        synth.setNote(1244.5);                    
                    }
                    //synth.setNote(622.25);

                    //synth.Synth(line,622.25);
                }
                if(e.getKeyCode() == KeyEvent.VK_1 || (sineWaveButton.isSelected()==true))//If 1 is pressed or the sineWave button is pressed, choice 1 will now be used
                {
                    choice = 1;
                    infoForWaves1.setText("A sine wave is a type of periodic sinusoidal wave that contains no harmonics.");
                    infoForWaves2.setText("This results in a very clear sound.");
                    infoForWaves3.setText("All other types of waves are built upon this fundamental waveform");
                    infoForWaves4.setText("");
                    infoForWaves5.setText("");
                    infoForWaves6.setText("");
                    infoForWaves7.setText("");
                }
                else if(e.getKeyCode() == KeyEvent.VK_2 || (squareWaveButton.isSelected()==true))
                {
                    choice = 2;
                    infoForWaves1.setText("A square wave is a non-sinusoidal periodic wave that contains ");
                    infoForWaves2.setText("many harmonics because of it's unique shape.");
                    infoForWaves3.setText("This type of wave is supposed to have an instantaneous transition from high to low,");
                    infoForWaves4.setText("but due to having a finite sample rate, we cannot achieve this instant transition");
                    infoForWaves5.setText("from high to low, thus having a rise and fall action."); 
                    infoForWaves6.setText("");
                    infoForWaves7.setText("");
                }
                else if(e.getKeyCode() == KeyEvent.VK_3 || (fluteWaveButton.isSelected()==true))
                {
                    choice = 3;
                    infoForWaves1.setText("This wave was originally supposed to be a Triangle Wave but");
                    infoForWaves2.setText("after countless hours testing the waveform, it became clear");
                    infoForWaves3.setText("that what was created, was a very-amplified sine wave. Enjoy!");
                    infoForWaves4.setText("");
                    infoForWaves5.setText("");
                    infoForWaves6.setText("");
                    infoForWaves7.setText("");
                }
                else if(e.getKeyCode() == KeyEvent.VK_4 || (noiseButton.isSelected()==true))
                {
                    choice = 4;
                    infoForWaves1.setText("This is a Noise wave that is used to create percussive sounds.");
                    infoForWaves2.setText("By setting a short release, one can create a snare drum sound.");
                    infoForWaves3.setText("This waveform is not random like most noise-generators, due to");
                    infoForWaves4.setText("the fact that the waveform is created by heavily distorting a");
                    infoForWaves5.setText("square wave, which explains why there are so many harmonics.");
                    infoForWaves6.setText("");
                    infoForWaves7.setText("");
                }
                else if(e.getKeyCode() == KeyEvent.VK_5 || (distortSineButton.isSelected()==true))
                {
                    choice = 5;
                    infoForWaves1.setText("This was my first attempt at creating Frequency-Modulation but it");
                    infoForWaves2.setText("was clear that it was something a lot more different than usual FM synthesis");
                    infoForWaves3.setText("What is happening is that, over time, the amplitude of the waveform is being");
                    infoForWaves4.setText("multiplied by a value that also increases over time.");
                    infoForWaves5.setText("");
                    infoForWaves6.setText("");
                    infoForWaves7.setText("");
                }
                else if(e.getKeyCode() == KeyEvent.VK_6 || (fmButton.isSelected()==true))
                {
                    choice = 6;
                    infoForWaves1.setText("Frequency-Modulation is a type of synthesis that uses one or more oscillators");
                    infoForWaves2.setText("to modulate the frequency and amplitude of other oscillators.");
                    infoForWaves3.setText("In this waveform, there are a total of 3 oscillators.");
                    infoForWaves4.setText("One is running at the user's frequency, another is running at a 1:4 ratio,");
                    infoForWaves5.setText("and the third is running at a 1:2 ratio. This gives the waveform extra harmonics,");
                    infoForWaves6.setText("since all three are individually sine waves,");
                    infoForWaves7.setText("which, you may recall, have no inherent harmonics.");
                }
                else if(e.getKeyCode() == KeyEvent.VK_7 || (echoSquareButton.isSelected()==true))
                {
                    choice = 7;
                    infoForWaves1.setText("This wave was originally a test to figure out envelope generators within Java.");
                    infoForWaves2.setText("As I persisted, I realized that instead of creating a volume envelope, I was somehow");
                    infoForWaves3.setText("creating a low-frequency oscillator(LFO), that modulated the output of the waveform.");
                    infoForWaves4.setText("This allows for the wave to come in and out of the audible spectrum, and gives an");
                    infoForWaves5.setText("echo-like sound. The original wave is a square wave.");
                    infoForWaves6.setText("");
                    infoForWaves7.setText("");
                }

                clock.start();//Starts the clock
                line.drain();//Sends the data from line to the speakers
                synth.Synth(line,synth.getNote(), choice);//Plays the synth based on type of waveform and note being played

            }

            public void keyReleased(KeyEvent e)
            {
            }

            public void keyTyped(KeyEvent e)
            {
            }

        }

        MIDI AcListen = new MIDI();

        frame.addKeyListener(AcListen);
        frame2.add(panel);
        panel.add(volumeSlider);
        panel.add(release);
        panel.add(sineWaveButton);
        panel.add(squareWaveButton);
        panel.add(fluteWaveButton);
        panel.add(noiseButton);
        panel.add(distortSineButton);
        panel.add(fmButton);
        panel.add(echoSquareButton);
        panel.add(oct1Button);
        panel.add(oct2Button);
        panel.add(oct3Button);
        panel.add(oct4Button);
        panel.add(infoForKeys);
        panel.add(infoForWaves1);
        panel.add(infoForWaves2);
        panel.add(infoForWaves3);
        panel.add(infoForWaves4);
        panel.add(infoForWaves5);
        panel.add(infoForWaves6);
        panel.add(infoForWaves7);
        frame2.setVisible(true);

        line.drain();
    }

}