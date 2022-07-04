package com.github.youssefwadie.wuzzufscrapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String path = null;
        StringBuilder keyword = new StringBuilder(100);

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("--binary-path")) {
                    if (args.length == i + 1) {
                        throw new RuntimeException("--binary-path requires one argument, found: " + 0);
                    }
                    path = args[i + 1];
                } else {
                    keyword.append(args[i]).append(' ');
                }
            }
        }
        RequestsManager requestsManager = RequestsManager.getInstance(path);
        List<Job> searchResults = null;
        try {
            ParseService parseService = ParseService.getInstance();
            searchResults = parseService.search(keyword.toString().trim());
            parseService.parseJobs(searchResults);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        } finally {
            requestsManager.close();
            if(searchResults != null) {
                File file = new File("out.json");
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(file, searchResults);
                System.out.println("saved to: " + file.getAbsolutePath());
            }
        }
    }
}