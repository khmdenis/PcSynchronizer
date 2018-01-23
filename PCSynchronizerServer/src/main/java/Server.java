import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {

    private ServerSocket serverSocket;
    private String path;

    public Server(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(1755);
        while (true) {
            try (
                    Socket connectionSocket = serverSocket.accept();
                    DataInputStream inFromClient = new DataInputStream(connectionSocket.getInputStream());
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            ) {
                File mainDirectory = new File(path);
                synchronizeFolder(mainDirectory, outToClient, inFromClient);
            }
        }
    }

    public void stop() throws IOException {
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    private void synchronizeFolder(File folder, DataOutputStream out, DataInputStream in) throws IOException {
        List<File> sentFiles = new ArrayList<>();
        List<String> existOnClientFiles = new ArrayList<>();
        List<File> existOnServerFiles = Arrays.asList(folder.listFiles());

        int countOfFilesOnClientSide = in.readInt();

        for (int i = 0; i < countOfFilesOnClientSide; i++) {
            String fileName = in.readUTF();
            existOnClientFiles.add(fileName);
        }

        for(File file : existOnServerFiles) {
            if(existOnClientFiles.contains(file.getName())
                    && !file.isDirectory()) {
                System.out.println(file.getName() + "already exist on client side");
                continue;
            }
            sentFiles.add(file);
        }
        out.writeInt(sentFiles.size());
        out.flush();
        int n;
        byte[]buf = new byte[4092];
        for(File file : sentFiles) {
            boolean isDir = file.isDirectory();
            out.writeBoolean(isDir);
            out.writeUTF(file.getName());
            if(isDir) {
                synchronizeFolder(file, out, in);
                continue;
            }
            out.writeLong(file.length());
            FileInputStream fis = new FileInputStream(file);
            while((n = fis.read(buf)) != -1){
                out.write(buf,0,n);
                out.flush();
            }
            fis.close();
        }
    }
}
