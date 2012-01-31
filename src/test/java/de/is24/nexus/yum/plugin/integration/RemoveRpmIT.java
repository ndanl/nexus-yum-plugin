package de.is24.nexus.yum.plugin.integration;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.junit.Test;

public class RemoveRpmIT extends AbstractNexusTestBase {

  private static final String NEW_REPO_ID = "remove-test-repo";
  private static final String GROUP_ID = "test";
  private static final String ARTIFACT_VERSION = "0.0.1-TEST";
  private static final String DUMMY_ARTIFACT_1 = "dummy-artifact-foo";
  private static final String DUMMY_ARTIFACT_2 = "dummy-artifact-bla";

  @Test
  public void shouldRemoveRpmFromYumRepoIfRemovedByWebGui() throws Exception {
    givenTestRepository(NEW_REPO_ID);
    Thread.sleep(5000);
    assertEquals(deployRpm(DUMMY_ARTIFACT_1, GROUP_ID, ARTIFACT_VERSION, NEW_REPO_ID), SC_CREATED);
    Thread.sleep(5000);
    assertEquals(deployRpm(DUMMY_ARTIFACT_2, GROUP_ID, ARTIFACT_VERSION, NEW_REPO_ID), SC_CREATED);
    Thread.sleep(5000);
    executeDelete("/repositories/" + NEW_REPO_ID + "/content/" + GROUP_ID + "/" + DUMMY_ARTIFACT_1);
    Thread.sleep(5000);
    String primaryXml = gzipResponseContent(executeGetWithResponse(NEXUS_BASE_URL + "/content/repositories/" + NEW_REPO_ID
        + "/repodata/primary.xml.gz"));
    assertThat(primaryXml, not(containsString(DUMMY_ARTIFACT_1)));
  }

  private void executeDelete(String url) throws AuthenticationException, UnsupportedEncodingException, IOException,
      ClientProtocolException {
    HttpDelete request = new HttpDelete(SERVICE_BASE_URL + url);
    setCredentials(request);
    HttpResponse response = client.execute(request);
    assertEquals(SC_NO_CONTENT, statusCode(response));
  }
}