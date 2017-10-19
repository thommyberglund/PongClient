package com.company;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Terminal terminal = TerminalFacade.createTerminal(System.in,
                System.out, Charset.forName("UTF8"));
        terminal.enterPrivateMode();
        String serverName = "192.168.25.158";
        //String serverName = "localhost";
        int port = 5000;
        try {
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);

            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            //System.out.println(in.readUTF());

            out.writeUTF("Hello from " + client.getLocalSocketAddress());
            Client ch = new Client(client);
            ch.start();

            while(true){
                Key key;
                do {
                    Thread.sleep(5);
                    key = terminal.readInput();

                }
                while (key == null);


                switch (key.getKind()) {
                    case ArrowDown:
                        System.out.println("Sending 1");
                        arrowDown(terminal);
                        ch.send("1");
                        break;
                    case ArrowUp:
                        System.out.println("Sending 2");
                        arrowUp(terminal);
                        ch.send("2");
                        break;

                }
                // client.close();
            }

        }catch(IOException e) {
            e.printStackTrace();
        }


    }

    private static void arrowUp(Terminal terminal)throws FileNotFoundException {

        terminal.clearScreen();
        terminal.setCursorVisible(false);
        String banner = "up";
        File fileBanner = new File(banner);
        Scanner printBanner = new Scanner(fileBanner);

        int q = 1;
        while (printBanner.hasNextLine()) {
            String tempLine = printBanner.nextLine();
            terminal.moveCursor(12, q++);
            for (int i = 0; i < tempLine.length(); i++) {
                terminal.putCharacter(tempLine.charAt(i));
            }
        }
    }

    private static void arrowDown(Terminal terminal)throws FileNotFoundException {

        terminal.clearScreen();
        terminal.setCursorVisible(false);
        String banner = "down";
        File fileBanner = new File(banner);
        Scanner printBanner = new Scanner(fileBanner);

        int q = 1;
        while (printBanner.hasNextLine()) {
            String tempLine = printBanner.nextLine();
            terminal.moveCursor(12, q++);
            for (int i = 0; i < tempLine.length(); i++) {
                terminal.putCharacter(tempLine.charAt(i));
            }
        }
    }
}



class Client extends Thread
{
    private Socket conn;

    Client(Socket conn)
    {
        this.conn = conn;
    }

    public void send(String s) throws IOException {
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeUTF(s);

    }

    public void run()
    {
        String line;

        try
        {
            DataInputStream in = new DataInputStream(conn.getInputStream());
            //Now start reading input from client
            while((line = in.readUTF()) != null && !line.equals("."))
            {
                //reply with the same message, adding some text
                System.out.println(line);



            }

            //client disconnected, so close socket
            conn.close();
        }

        catch (IOException e)
        {
            System.out.println("IOException on socket : " + e);
            e.printStackTrace();
        }
    }
}





