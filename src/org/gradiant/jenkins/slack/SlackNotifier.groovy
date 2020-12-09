package org.gradiant.jenkins.slack

String appendHelperUrlsToMessage( String message ) {
  def logsUrl = helper.getConsoleLogsUrl()
  def testsUrl = helper.getTestsResultUrl()
  def artifactsUrl = helper.getArtifactsUrl()

  message = message += "\n<${logsUrl}|ConsoleLog>"
  message = message += "\n<${testsUrl}|Test Result>"
  message = message += "\n<${artifactsUrl}|Artifacts>"

  return message
}

void notifyMessage( custom_message ) {
  SlackFormatter formatter = new SlackFormatter()
  SlackSender sender = new SlackSender()
  JenkinsStatus status = new JenkinsStatus()

  def message = formatter.format custom_message
  def colors = new Color()
  
  sender.send message, colors.blue()
}

void notifyStart() {
  SlackFormatter formatter = new SlackFormatter()
  SlackSender sender = new SlackSender()
  JenkinsStatus status = new JenkinsStatus()

  def message = formatter.format 'Build started...'
  def color = status.getStatusColor()

  sender.send message, color
}


void notifyError(Throwable err) {
  def formatter = new SlackFormatter()
  def sender = new SlackSender()
  def color = new Color().red()

  def message = formatter.format ":interrobang: An error occurred "

  if ( env.CURRENT_STEP != null ) {
    message += " while executing ${env.CURRENT_STEP}"
  }

  message += " ${err}"

  message = appendHelperUrlsToMessage( message )
  sender.send message, color
}

boolean shouldNotNotifySuccess(statusMessage) {
  Config config = new Config()
  return statusMessage == 'Success' && !config.getNotifySuccess()
}

void notifyResult() {
  JenkinsHelper helper = new JenkinsHelper()
  JenkinsStatus status = new JenkinsStatus()
  SlackFormatter formatter = new SlackFormatter()
  SlackSender sender = new SlackSender()
  Config config = new Config()

  def statusMessage = status.getStatusMessage()

  if(shouldNotNotifySuccess(statusMessage)) {
    println("SlackNotifier - No notification will be send for SUCCESS result")
    return
  }

  def color = status.getStatusColor()
  def duration = helper.getDuration()

  String changes = null
  if(config.getChangeList()) changes = helper.getChanges().join '\n'

  String testSummary = null
  if (config.getTestSummary()) {
    JenkinsTestsSummary jenkinsTestsSummary = new JenkinsTestsSummary()
    testSummary = jenkinsTestsSummary.getTestSummary()
  }

  def message = formatter.format "${statusMessage} after ${duration}", changes, testSummary

  message = appendHelperUrlsToMessage( message )

  sender.send message, color
}

void notifyResultFull() {
  env.TEST_SUMMARY = true
  env.CHANGE_LIST = true
  env.NOTIFY_SUCCESS = true
  notifyResult()
}