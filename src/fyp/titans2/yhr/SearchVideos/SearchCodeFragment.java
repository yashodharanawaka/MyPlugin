package fyp.titans2.yhr.SearchVideos;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;

public class SearchCodeFragment extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get all the required data from data keys
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Document document = editor.getDocument();

        // Work off of the primary caret to get the selection info
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();

        String codeFragment = document.getText(TextRange.create(start,end));
        System.out.println(codeFragment);

//        String codeFragment = "public class MyClass { public static void main(String[] args) { double myDouble = 9.78; int myInt = (int) myDouble; // Manual casting: double to int System.out.println(myDouble);   // Outputs 9.78 System.out.println(myInt);      // Outputs 9 } }";
        codeFragment = codeFragment.replaceAll("\\s|\\{|\\(|\\[|]|\\)|}|\"|\\*", "%20");
        String url = "http://127.0.0.1:5000/search?context="+codeFragment;

        HttpURLConnection httpClient =
                null;
        try {
            httpClient = (HttpURLConnection) new URL(url).openConnection();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // optional default is GET
        try {
            httpClient.setRequestMethod("GET");
        } catch (ProtocolException ex) {
            ex.printStackTrace();
        }

        //add request header
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = 0;
        try {
            responseCode = httpClient.getResponseCode();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        try {
            Desktop desktop = java.awt.Desktop.getDesktop();
            URI oURL = new URI(url);
            desktop.browse(oURL);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // De-select the text range that was just replaced
        primaryCaret.removeSelection();
    }


    @Override
    public void update(@NotNull final AnActionEvent e) {
        // Using the event, evaluate the context, and enable or disable the action.
        // Set the availability based on whether a project is open
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);

        //Set visibility only in case of existing project and editor and if a selection exists
        e.getPresentation().setEnabledAndVisible( project != null
                && editor != null
                && editor.getSelectionModel().hasSelection() );
    }
}