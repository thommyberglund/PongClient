package com.company;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class StartGame {
    public void startGameApp(Terminal terminal) throws IOException, InterruptedException, MidiUnavailableException, InvalidMidiDataException {

        Sequencer sequencer = startMusic();

        doThread threading = new doThread(sequencer);

        threading.start();

        showBanner(terminal);

        menu(terminal, sequencer, threading);


    }

    private void menu(Terminal terminal, Sequencer sequencer, doThread threading) throws InterruptedException {

        String serverName = "192.168.25.147";
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
            client_handler ch = new client_handler(client);
            ch.start();

            int position = 1;

            while(true){
                Key key;
                do {
                    Thread.sleep(5);
                    key = terminal.readInput();

                }
                while (key == null);


                switch (key.getKind()) {
                        case ArrowDown:
                            terminal.moveCursor(18,20);
                            terminal.putCharacter(' ');
                            terminal.moveCursor(18,22);
                            terminal.putCharacter('*');
                            position = 2;
                            System.out.println("Sending 2");
                            ch.send("2");
                            break;
                        case ArrowUp:
                            terminal.moveCursor(18,20);
                            terminal.putCharacter('*');
                            terminal.moveCursor(18,22);
                            terminal.putCharacter(' ');;
                            position = 1;
                            System.out.println("Sending 1");
                            ch.send("1");
                            break;
                        case Enter:
                            if(position == 1) {
                                threading.halt(sequencer);
                                break;
                            }
                            if (position == 2) {
                                System.exit(0);
                                threading.halt(sequencer);
                            }
                    }
               // client.close();
            }

        }catch(IOException e) {
            e.printStackTrace();
        }

    }

    private void showBanner(Terminal terminal) throws FileNotFoundException {
        int q = 3;
        String banner = "intro.txt";
        File fileBanner = new File(banner);
        Scanner printBanner = new Scanner(fileBanner);
        while(printBanner.hasNextLine()) {
            String tempLine = printBanner.nextLine();
            terminal.moveCursor(12,q++);
            for (int i = 0; i< tempLine.length();i++) {
                terminal.putCharacter(tempLine.charAt(i));
            }
        }
        terminal.setCursorVisible(false);

        String startGame = "Start Game";
        String quitGame = "Quit Game";

        terminal.moveCursor(20,20);
        for (int i = 0; i< startGame.length();i++) {
            terminal.putCharacter(startGame.charAt(i));
        }

        terminal.moveCursor(20,22);
        for (int i = 0; i< quitGame.length();i++) {
            terminal.putCharacter(quitGame.charAt(i));
        }

        terminal.moveCursor(18,20);
        terminal.putCharacter('*');
    }

    private Sequencer startMusic() throws MidiUnavailableException, IOException, InvalidMidiDataException {
        Sequencer sequencer = MidiSystem.getSequencer();

        sequencer.open();
        String path3 = "tetris.mid";
        InputStream tetris = new BufferedInputStream(new FileInputStream(new File(path3)));

        sequencer.setSequence(tetris);
        return sequencer;
    }
}

class client_handler extends Thread
{
    private Socket conn;

    client_handler(Socket conn)
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

