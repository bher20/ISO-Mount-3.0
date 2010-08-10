/*This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

/**
 * 
 */

/**
 * @author Brad Herring
 *
 */
public class Main 
{	
	static String iniLocation = "";
	static String isoLocation = "";
	static String vcdMountLocation = "";
	static String bd = "";
	static String driveLet = "";
	static String displayChanger = "";
	static int driveNumber = 0;
	static int refreshRate = 60;
	static int width = 0;
	static int height = 0;
	static int depth = 32;
	static int wait = 0;
	static boolean debugMode = false;
	static boolean useIniFile = false;
	static JFrame f;
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws AWTException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, AWTException, InterruptedException 
	{	
		 f = new JFrame("ISO Mounter");
		 
		initilizeProgram(args);
		
	}
	
	private static void initilizeProgram(String[] env) throws IOException
    {
        readArgs(env);

        if (useIniFile)
        {
            processIniFile();
        }

        debugInfo();

        checkForFileNameFPS();

        try 
        {
			doMountProcess();
		} 
        
        catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
    }
	

	private static void debugInfo()
     {
         String toShow = "";

         toShow += "IniLocation :" + iniLocation + "\r\n" +
             "ISO :" + isoLocation + "\r\n" +
             "Debug :" + debugMode + "\r\n" +
             "VCD :" + vcdMountLocation + "\r\n" +
             "Letter :" + driveLet + "\r\n" +
             "BD :" + bd + "\r\n" +
             "Refresh :" + refreshRate + "\r\n" +
             "DC :" + displayChanger + "\r\n" +
             "W :" + width + "\r\n" +
             "H :" + height + "\r\n" +
             "D :" + depth + "\r\n" +
             "Wait :" + wait + "\r\n";

         writeToInfoBox(toShow);
     }
	 
	 
	 private static void writeToInfoBox(String strIn)
	 {
		 if (debugMode)
		 {
			 utiliti.FileOps.file.writeToLog (strIn, "debug.log");
		 }
	 }
	 
	 
	 private static void checkForFileNameFPS()
     {
         String regex = "[^.]([0-9]+)fps";
         String fps = "";
         
         Pattern myPattern = Pattern.compile(regex);

         
         Matcher match = myPattern.matcher(isoLocation);

         if (match.matches())
         {
             fps = match.group(0);
             fps = fps.toLowerCase().replace("fps", "");

             refreshRate = Integer.parseInt(fps);
         }

     }
	
	
	private static void readArgs(String[] arrayIn)
    {
        String[] args = arrayIn;
        String currentArg = "", 
                argProp = "",
                toSendToInfoBox = "";

        useIniFile = false;



        for(int i = 0; i < args.length; i++)
        {

            try
            {
                currentArg = args[i];
                argProp = args[i + 1];


                if (currentArg.equals("-useIni"))
                {
                    iniLocation = argProp;

                    toSendToInfoBox = "";
                    useIniFile = true;
                }

                else if (currentArg.equals("-debug"))
                {
                    debugMode = true;

                    i++;
                }

                else if (currentArg.equals("-iso"))
                {
                    isoLocation = argProp;

                    i++;
                }

                else if (currentArg.equals("-vcd"))
                {
                    vcdMountLocation = argProp;

                    i++;
                }

                else if (currentArg.equals("-driveNum"))
                {
                    driveNumber = Integer.parseInt(argProp);

                    i++;
                }

                else if (currentArg.equals("-bd"))
                {
                    bd = argProp;

                    i++;
                }

                else if (currentArg.equals("-driveLet"))
                {
                    driveLet = argProp;

                    i++;
                }

                else if (currentArg.equals("-dc"))
                {
                    displayChanger = argProp;

                    i++;
                }

                else if (currentArg.equals("-refresh"))
                {
                    refreshRate = Integer.parseInt(argProp);

                    i++;
                }

                else if (currentArg.equals("-width"))
                {
                    width = Integer.parseInt(argProp);

                    i++;
                }

                else if (currentArg.equals("-height"))
                {
                    height = Integer.parseInt(argProp);

                    i++;
                }

                else if (currentArg.equals("-depth"))
                {
                    depth = Integer.parseInt(argProp);

                    i++;
                }

                else if (currentArg.equals("-wait"))
                {
                    wait = Integer.parseInt(argProp);

                    i++;
                }



            }
            catch (Exception e)
            {
                
                ;
            }

            if (!useIniFile)
            {
                System.out.println(toSendToInfoBox);
            }
        }
    }
	
	private static void processIniFile() throws IOException
    {
        BufferedReader iniReader;
        String currentLine;
        ArrayList<String> args = new ArrayList<String>();
        String[] temp, 
        		 toDo;


        iniReader = new BufferedReader(new FileReader(iniLocation));
        
        currentLine = iniReader.readLine();



        while (currentLine != null)
        {
            temp = currentLine.split("=");

            for (int i = 0; i < temp.length; i++)
            {
                if (i == 0)
                {
                    args.add("-" + temp[i]);
                }

                else
                    args.add(temp[i]);
            }

            currentLine = iniReader.readLine();
        }

        iniReader.close();
        
        toDo = new String[args.size()];
        
        for (int i = 0; i <  args.size(); i++)
        {
        	toDo[i] = args.get(i);
        }

        readArgs(toDo);
    }
	

	
	 private static void doMountProcess() throws IOException, InterruptedException 
	 {		
		 
	//Mount the iso
		if (driveLet.equals(""))
        {
			String[] command = {vcdMountLocation, "/d=" + driveNumber, isoLocation};
            writeToInfoBox("Mounting iso: " + +'"' + isoLocation + '"' + " to drive number: " + driveNumber + "\r\n");


            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
        }

        else
        {
        	String[] command = {vcdMountLocation, "/l=" + driveLet, isoLocation};
            writeToInfoBox("Mounting iso: " + "" + isoLocation + "" + " to drive letter: " + driveLet + "\r\n");


            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
        }

		
		
	//Start the bd software
		
        if (height != 0)
        {
        	writeToInfoBox("String the bd software!");
        	String[] command = {displayChanger, "-width=" + width, "-height=" + height, "-refresh=" + refreshRate, bd};
        			
            

            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();

        }

        else if (!displayChanger.equals(""))
        {
        	writeToInfoBox("String the bd software!");
            String[] command = {displayChanger, bd};
        			
            

            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
        }
        
        else
        {
        	writeToInfoBox("String the bd software!");
            String[] command = {bd};
        			
            

            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
        }


       if (driveLet.equals(""))
        {
            writeToInfoBox("Unmounting iso: " + '"' + isoLocation + '"' + " from drive number: " + driveNumber + "\r\n");


            String[] command = {vcdMountLocation, "/u", "/d=" + driveNumber, isoLocation};
        			
            

            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
        }

        else
        {
            writeToInfoBox("Unounting iso: " + '"' + isoLocation + '"' + " from drive letter: " + driveLet + "\r\n");
        
            String[] command = {vcdMountLocation, "/u", "/l=" + driveLet, isoLocation};
        			
            

            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
        } 
	}
}
