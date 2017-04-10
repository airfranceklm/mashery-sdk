package com.afkl.generic.mashery.model;

public class MasheryCors {

    private boolean allDomainsEnabled;

    private Integer maxAge;

    private boolean cookiesAllowed;

    private String[] domainsAllowed;
    private String[] headersAllowed;
    private String[] headersExposed;
    private boolean subDomainMatchingAllowed;

    public boolean isAllDomainsEnabled() {
        return allDomainsEnabled;
    }

    public void setAllDomainsEnabled(boolean allDomainsEnabled) {
        this.allDomainsEnabled = allDomainsEnabled;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public boolean isCookiesAllowed() {
        return cookiesAllowed;
    }

    public void setCookiesAllowed(boolean cookiesAllowed) {
        this.cookiesAllowed = cookiesAllowed;
    }

    public String[] getDomainsAllowed() {
        return domainsAllowed;
    }

    public void setDomainsAllowed(String[] domainsAllowed) {
        this.domainsAllowed = domainsAllowed;
    }

    public String[] getHeadersAllowed() {
        return headersAllowed;
    }

    public void setHeadersAllowed(String[] headersAllowed) {
        this.headersAllowed = headersAllowed;
    }

    public String[] getHeadersExposed() {
        return headersExposed;
    }

    public void setHeadersExposed(String[] headersExposed) {
        this.headersExposed = headersExposed;
    }

    public boolean isSubDomainMatchingAllowed() {
        return subDomainMatchingAllowed;
    }

    public void setSubDomainMatchingAllowed(boolean subDomainMatchingAllowed) {
        this.subDomainMatchingAllowed = subDomainMatchingAllowed;
    }
}
