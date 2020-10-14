definition(
    name: "Controlled by Modes",
    namespace: "bobbyD",
    author: "Bobby Dobrescu",
    description: "Perform actions when modes change",
    category: "Convenience",
    iconUrl: "",
    iconX2Url: "")

preferences {
	page(name: "mainPage")
}

def mainPage() {
	dynamicPage(name: "mainPage", title: " ", install: true, uninstall: true) {
		section ("Day") {
			input "switchesOnD", "capability.switch", title: "Devices to turn on in the Morning", submitOnChange: true, required: false, multiple: true
            input "switchesOffD", "capability.switch", title: "Devices to turn off in the Morning", submitOnChange: true, required: false, multiple: true 
            input "dimmersLvlD", "capability.switchLevel", title: "Dimmers to set level in the Morning", submitOnChange: true, required: false, multiple: true
            if (dimmersLvlD) { 
				input "dimmerValueD", "number", title: "Dimmer Level", submitOnChange: true, required: true
			}      
            input "bulbsTempClrD", "capability.colorTemperature", title: "Bulbs to Set Color Temperature in the Morning", submitOnChange: true, required: false, multiple: true
            input "locksD", "capability.lock", title: "Locks to lock in the Morning", submitOnChange: true, required: false, multiple: true
            input "messageD", "bool", title: "Enable TTS", width: 6, submitOnChange:true
            if (messageD) {
                input "speakDevD", "capability.speechSynthesis", title: "Select TTS Device", submitOnChange:true, required: false, multiple: true
                input "volumeValueD", "number", title: "Audio Level", submitOnChange: true, required: true
            }
        }
		section("Evening") {
			input "switchesOnE", "capability.switch", title: "Devices to turn on in the Evening", submitOnChange: true, required: false, multiple: true
            input "switchesOffE", "capability.switch", title: "Devices to turn off in the Evening", submitOnChange: true, required: false, multiple: true 
            input "dimmersLvlE", "capability.switchLevel", title: "Dimmers to set level in the Evening", submitOnChange: true, required: false, multiple: true
            if (dimmersLvlE) { 
				input "dimmerValueE", "number", title: "Dimmer Level", submitOnChange: true, required: true
			}                
            input "bulbsTempClrE", "capability.colorTemperature", title: "Bulbs to Set Color Temperature in the Evening", submitOnChange: true, required: false, multiple: true
            input "locksE", "capability.lock", title: "Select Lock", submitOnChange: true, required: false, multiple: true
            input "messageE", "bool", title: "Enable TTS", width: 6, submitOnChange:true
            if (messageE) {
                input "speakDevE", "capability.speechSynthesis", title: "Select TTS Device", submitOnChange:true, required: false, multiple: true
                input "volumeValueE", "number", title: "Audio Level", submitOnChange: true, required: true
            }
		} 
        section("Night") {
			input "switchesOnN", "capability.switch", title: "Devices to turn on in the Evening", submitOnChange: true, required: false, multiple: true
            input "switchesOffN", "capability.switch", title: "Devices to turn off in the Evening", submitOnChange: true, required: false, multiple: true 
            input "dimmersLvlN", "capability.switchLevel", title: "Dimmers to set level in the Evening", submitOnChange: true, required: false, multiple: true
            if (dimmersLvlN) { 
				input "dimmerValueN", "number", title: "Dimmer Level", submitOnChange: true, required: true
			} 
            input "bulbsTempClrN", "capability.colorTemperature", title: "Bulbs to Set Color Temperature in the Evening", submitOnChange: true, required: false, multiple: true
            input "locksN", "capability.lock", title: "Select Lock", submitOnChange: true, required: false, multiple: true
            input "messageN", "bool", title: "Enable TTS", width: 6, submitOnChange:true
            if (messageN) {
                input "speakDevN", "capability.speechSynthesis", title: "Select TTS Device", submitOnChange:true, required: false, multiple: true
                input "volumeValueN", "number", title: "Audio Level", submitOnChange: true, required: true
            }
        }
        section("Away") {
			input "switchesOnA", "capability.switch", title: "Devices to turn on in the Evening", submitOnChange: true, required: false, multiple: true
            input "switchesOffA", "capability.switch", title: "Devices to turn off in the Evening", submitOnChange: true, required: false, multiple: true 
            input "dimmersLvlA", "capability.switchLevel", title: "Dimmers to set level in the Evening", submitOnChange: true, required: false, multiple: true
            if (dimmersLvlA) { 
				input "dimmerValueA", "number", title: "Dimmer Level", submitOnChange: true, required: true
			}             
            input "bulbsTempClrA", "capability.colorTemperature", title: "Bulbs to Set Color Temperature in the Evening", submitOnChange: true, required: false, multiple: true
            input "locksA", "capability.lock", title: "Select Lock", submitOnChange: true, required: false, multiple: true
            input "messageA", "bool", title: "Enable TTS", width: 6, submitOnChange:true
            if (messageA) {
                input "speakDevA", "capability.speechSynthesis", title: "Select TTS Device", submitOnChange:true, required: false, multiple: true
                input "volumeValueA", "number", title: "Audio Level", submitOnChange: true, required: true
            }
        }
        section() {   
            input "logging", "bool", title: "Enable logging", width: 6
        }    
	}
}

def installed() {
	initialize()
}

def updated() {
	unsubscribe()
    unschedule()
	initialize()
}

def initialize() {
	subscribe(location, "mode", modeHandler)
}

def modeHandler(evt) {
    if (logging) log.info "Got mode change: ${evt.value}"
	switch(evt.value) {
		case "Day": 
			dayActions()
			break;
		case "Evening":
			eveningActions()
			break;
		case "Night": 
			nightActions()
			break;
		case "Away": 
			awayActions()
			break;	
    }
}
    
def dayActions() {
    if (logging) log.warn "Executing day actions"
    def message = "Good morning"
    if (switchesOnD) {
		switchesOnD.each { it.on() }
	} 
    if (dimmersLvlM) {
		for (dev in dimmersLvlD) {
        dev.setLevel(dimmerValueM) 
            if (logging) log.debug "Setting level to ${dimmerValueM} % on " + dev.getDisplayName()                                
      }
	}
    if (bulbsTempClrM) {
        for (dev in bulbsTempClr) {
		dev.setColorTemperature(3500)
		 if (logging) log.debug "Setting CT to 3500 on " + dev.getDisplayName()
        }
    }
	if (switchesOffM) {
		switchesOffmorning.each{ it.off() }
	}
	if (speakDevD) {
		speakDevD.each { it.setVolume(volumeValueD) }    
		speakDevD.each { it.speak(message) }
    }
}

def eveningActions() {
    def message = ''
    message = "Good evening, as you requested, I am locking the doors and turning the outdoor lights on"
    if (logging) log.warn "Executing evening actions"
    if (switchesOn) {
		switchesOn.each { it.on() }
	}
	if (dimmersLvl) {
		for (dev in dimmersLvl) {
        dev.setLevel(10) 
         if (logging) log.debug "Setting level to 10% on " + dev.getDisplayName()                                
      }
	}
    if (bulbsTempClr) {
        for (dev in bulbsTempClr) {
		dev.setColorTemperature(2700)
		 if (logging) log.debug "Setting CT to 2700 on " + dev.getDisplayName()
        }
    }
    if (switchesOffevening) {
		switchesOffevening.each{ it.off() }
	}
    if (locksU) {
		locksU.each{ it.lock() }
	}
	if (speakDevE) {
		speakDevE.each { it.setVolume(volumeValueE) }    
		speakDevE.each { it.speak(message) }
    }
}

def nightActions() {
    def message = ''
    message = "Good night, as you requested, I am locking the doors and setting the lights for a good night sleep"
    if (logging) log.warn "Executing night actions"
	
    if (dimmersLvl) {
		for (dev in dimmersLvl) {
        dev.setLevel(1) 
         if (logging) log.debug "Setting level to 1% on " + dev.getDisplayName()                                
      }
    }
	if (switchesOffnight) {
		switchesOffnight.each{ it.off() }
	}
        if (locksU) {
		locksU.each{ it.lock() }
	}
	if (speakDevN) {
		speakDevN.each { it.setVolume(volumeValueN) }    
		speakDevN.each { it.speak(message) }
    }
}

def awayActions() {
    def message = ''
    message = "Good bye, as you requested, I am locking the doors and making sure that all lights are off"
    if (logging) log.warn "Executing away actions"
	if (switchesOffaway) {
		switchesOffaway.each{ it.off() }
	}
    if (locksU) {
		locksU.each{ it.lock() }
	}
	if (speakDevA) {
		speakDevA.each { it.setVolume(volumeValueA) }    
		speakDevA.each { it.speak(message) }
    }
}
