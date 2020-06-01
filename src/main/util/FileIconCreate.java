package main.util;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class FileIconCreate {
    public static Icon getSmallIcon(File f ) {
        if ( f != null && f.exists() ) {
            FileSystemView fsv = FileSystemView.getFileSystemView();
            return(fsv.getSystemIcon( f ) );
        }
        return(null);
    }
}
