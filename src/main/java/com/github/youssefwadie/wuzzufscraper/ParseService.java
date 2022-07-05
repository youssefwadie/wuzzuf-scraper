package com.github.youssefwadie.wuzzufscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

public class ParseService {
    private final URI baseURI;

    private final RequestsManager requestsManager = RequestsManager.getInstance();

    private static ParseService instance = null;

    private ParseService() throws URISyntaxException {
        baseURI = new URI("https://wuzzuf.net/");
    }

    public static ParseService getInstance() throws URISyntaxException {
        if (instance == null) {
            instance = new ParseService();
        }
        return instance;
    }

    public List<Job> search(String keyword) throws URISyntaxException {
        // better to use URI builder but this will be overkill for this small project
        URI uri = new URI(baseURI.getScheme(), baseURI.getAuthority(), "/search/jobs/", "q=" + keyword, baseURI.getFragment());
        String response = requestsManager.sendGET(uri);
        List<Job> jobs = new LinkedList<>();
        Document searchDocument = Jsoup.parse(response);
        for (Element page : searchDocument.select("li.css-1q4vxyr")) {
            if (page.hasAttr("tabindex")) {
                int pageNumber = Integer.parseInt(page.text());
                jobs.addAll(parseJobsInSinglePage(keyword, pageNumber));
            }
        }
        return jobs;
    }

    private List<Job> parseJobsInSinglePage(String keyword, int pageNumber) throws URISyntaxException {
        URI uri = new URI(baseURI.getScheme(), baseURI.getAuthority(), "/search/jobs/", "q=" + keyword + "&start=" + (pageNumber - 1), baseURI.getFragment());
        String response = requestsManager.sendGET(uri);
        List<Job> jobs = new LinkedList<>();
        Document searchDocument = Jsoup.parse(response);

        Elements jobEntries = searchDocument.select("div.css-1gatmva > div > div > h2 >  a:nth-child(1)");
        for (Element jobEntry : jobEntries) {
            String href = jobEntry.attr("href");
            String title = jobEntry.text();
            URI jobURI = new URI(baseURI.getScheme(), baseURI.getAuthority(), href, baseURI.getQuery(), baseURI.getFragment());
            jobs.add(new Job(jobURI, title));
        }
        return jobs;
    }

    public void parseJobs(List<Job> jobs) {
        for (Job job : jobs) {
            System.out.println("parsing: " + job.getTitle());
            String response = requestsManager.sendGET(job.getUri());
            Document jobDocument = Jsoup.parse(response);
            Element location = jobDocument.selectFirst("strong.css-9geu3q:nth-child(4)");
            if (location != null) {
                job.setLocation(location.text());
            }
            Element companySection = jobDocument.selectFirst("section.css-1rhgoyg");
            Company company = new Company();
            if (companySection != null) {
                Element specializationSpan = companySection.selectFirst("span.css-xilyze");
                if (specializationSpan != null) {
                    company.setSpecialization(specializationSpan.text());
                }
                Element title = companySection.selectFirst("a.css-tdvcnh");
                if (title != null) {
                    company.setTitle(title.text());
                }
                job.setCompany(company);
                Element about = companySection.selectFirst("span.css-qamjgr");
                if (about != null) {
                    company.setAbout(about.text());
                }
                Element locationSpan = companySection.selectFirst("span.css-nhiaul");
                if (locationSpan != null) {
                    company.setLocation(locationSpan.text().split("\\.")[0].trim());
                }
            } else {
                company.setTitle("Confidential Company");
            }
            job.setCompany(company);

            Element jobDetailsSection = jobDocument.selectFirst("section.css-3kx5e2");
            if (jobDetailsSection != null) {
                for (Element jobDetail : jobDetailsSection.select("div.css-rcl8e5")) {
                    String key = jobDetail.selectFirst("span.css-wn0avc").text().split("\\.")[0].trim();
                    String val = jobDetail.selectFirst("span:nth-child(2) > span:nth-child(1)").text().trim();
                    job.addToDetails(key, val);
                }
            }
            Element jobDescriptionSection = jobDocument.selectFirst("section.css-ghicub:nth-child(4)");
            if (jobDescriptionSection != null) {
                for (Element jobDesc : jobDescriptionSection.select("li")) {
                    job.addToDescription(jobDesc.text());
                }
            }

            Element jobRequirementsSection = jobDocument.selectFirst("section.css-ghicub:nth-child(5)");
            if (jobRequirementsSection != null) {
                for (Element jobReq : jobRequirementsSection.select("li")) {
                    job.addToRequirements(jobReq.text());
                }
            }
            System.out.println("Parsed: " + job.getTitle());
        }
    }
}
