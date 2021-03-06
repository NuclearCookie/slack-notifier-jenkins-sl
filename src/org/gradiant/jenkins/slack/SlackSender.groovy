package org.gradiant.jenkins.slack

class SlackSender {

  private config = null
  private Script script = null

  public SlackSender( config, Script script ) {
    this.config = config
    this.script = script
  }

  public void sendBlocks( blocks ) {
    return this.send( this.config.Channel, blocks )
  }

  public void updateMessage( slackResponse, blocks ) {
    this.send( slackResponse.channelId, blocks, slackResponse.ts )
  }

  public void sendDirectMessage( String user_id, String message, String color ) {
    this.script.slackSend( channel: user_id, color: color, message: message )
  }

  private void send( channel_id, blocks, ts = null ) {
    def response = this.script.slackSend( channel: channel_id, teamDomain: this.config.SlackDomain, tokenCredentialId: this.config.SlackCredentials, blocks: blocks, timestamp: ts )
    return response
  }
}