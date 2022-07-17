package com.imdamilan.uuidauthenticator.velocity.fileauth

class AuthFile {
    var players: LinkedHashMap<String, String> = LinkedHashMap()
    constructor()
    constructor(players: LinkedHashMap<String, String>) { this.players = players }
}

