/* Version 1.2 */

definition(
    name: "Motion Locking",
    namespace: "BD",
    author: "Bobby Dobrescu",
    description: "Locking doors at quiet times",
    category: "Convenience",
    iconUrl: "",
    iconX2Url: "")

preferences {
	page(name: "mainPage")
}

def mainPage() {
	dynamicPage(name: "mainPage", title: " ", install: true, uninstall: true) {
		section ("Front Door") {
			input "fLock", "capability.lock", title: "Select Lock", submitOnChange: true, required: true, multiple: false
			input "fMotion", "capability.motionSensor", title: "Use these motion sensors", submitOnChange: true, required: false, multiple: false
            if (fMotion) input "fMinutes", "number", title: "Wait this number of minutes", submitOnChange: true, description: "Enter number of minutes"
            input "fContact", "capability.contactSensor", title: "Use these contact sensors", submitOnChange: true, required: false, multiple: false
        }
		section("Back door") {
			input "bLock", "capability.lock", title: "Select Lock", submitOnChange: true, required: true, multiple: false            
			input "bMotion", "capability.motionSensor", title: "Use these motion sensors", submitOnChange: true, required: false, multiple: false
            if (bMotion) input "bMinutes", "number", title: "Wait this number of minutes", submitOnChange: true, description: "Enter number of minutes"
            input "bContact", "capability.contactSensor", title: "Use these contact sensors", submitOnChange: true, required: false, multiple: false
		} 
        	section("Audio Messages") {	
		    input "speakDev", "capability.speechSynthesis", title: "Select TTS Device", submitOnChange:true, required: false, multiple: true
            input "introS", "bool", title: "Intro Sound", width: 6    
		}
        
        section() {
        input "modesY", "mode", title: "Only when mode is", multiple: true, submitOnChange: true
        input "logging", "bool", title: "Enable logging", width: 6
        }    
	}
}    
    
    
def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}

def initialize() {
	//Front Door
    subscribe(fLock, "lock.locked", fCancel)
    subscribe(fMotion, "motion", fHandler)
    state.fSchedule = false
    //Back Door
	subscribe(bLock, "lock.locked", bCancel)
    subscribe(bMotion, "motion", bHandler)
    state.bSchedule = false
}


//*********************//
// FRONT DOOR ACTIONS //
//*********************//

def fCancel(evt) {
    if (logging) log.debug "received front door ${evt.value} event"     
    if (state.fSchedule == true){
        if (logging) log.info "unscheduling front door timers"
        state.fSchedule = false
        unschedule()  
    }
    else if (logging) log.info "not taking action because nothing is scheduled for the front door"
}

def fHandler(evt) {
    def cMode = location.currentMode
    if (getModeOk()==true) {  
        if (logging) log.debug "received ${evt.value} motion event at the front door"
        if(evt.value == "inactive"){
           if(fLock.currentLock.contains("unlocked")) {
               if (logging) log.info "setting timer to lock the front door in $fMinutes minutes"
               state.fSchedule = true
               runIn(fMinutes * 60, fLocking)
           }
           else if (logging) log.info "not taking action because the front door lock is ${fLock.currentLock}"
        }
        else {
            if (state.fSchedule == true) {
                if (logging) log.info "front door motion is active, unscheduling timer"
                unschedule()
                state.fSchedule = false
            }
            else if (logging) log.info "not taking action because because nothing is scheduled for the front door"
        }
    }
    else if (logging) log.info "not taking action because mode is $cMode"
}
    
def fLocking() {
   if(fLock.currentLock.contains("unlocked")){
        if(fContact.currentContact.contains("open")){
            if (logging) log.warn "Front Door conctact is open, checking again in one minute"
            runIn(1 * 60, secondTry)
        }
       else {
           state.fSchedule = false
           if (logging) log.info "locking the front door"
           state.fSchedule = false
           fLock.lock()
       }
    }
    else if (logging) log.warn "not taking action because front door lock is ${fLock.currentLock}"
}
    
def secondTry() {
    if (fLock.currentLock.contains("unlocked")) {
       if (fContact.currentContact.contains("closed")){
           if (logging) log.info "locking the door after second try"
           state.fSchedule = false
           fLock.lock()
       }
       else {
            if (logging) log.warn "Front door conctact is still open checking again in one minute"
            runIn(1 * 60, thirdTry)    
        }
    }
    else log.info "not taking action because front door lock is already ${fLock.currentLock}"
}

def thirdTry() {
    if(fLock.currentLock.contains("unlocked")){
        log.error "locking the front door after third try, please check your contact sensor"
        def message = "Heads up, locking the front door while the door appears to be open" 
        if (speakDev) playMessage(message)
        state.fSchedule = false
        fLock.lock()
    }
}

//*********************//
// BACK DOOR ACTIONS //
//*********************//

def bCancel(evt) {
    if (logging) log.debug "received back door ${evt.value} event"
    if (state.bSchedule == true){
        if (logging) log.info "unscheduling back door timers"
        state.bSchedule = false
        unschedule()  
    }
    else if (logging) log.info "not taking action because nothing is scheduled for the back door"
}

def bHandler(evt) {
    def cMode = location.currentMode
    if (getModeOk()==true) {  
        if (logging) log.debug "received ${evt.value} motion event at the back door"
        if(evt.value == "inactive"){
           if(bLock.currentLock.contains("unlocked")) {
               if (logging) log.info "setting timer to lock the back door in $bMinutes minutes"
               state.bSchedule = true
               runIn(bMinutes * 60, bLocking)
           }
           else if (logging) log.info "not taking action because the back door lock is ${bLock.currentLock}"
        }
        else {
            if (state.bSchedule == true) {
                if (logging) log.info "back door motion is active, unscheduling timer"
                unschedule()
                state.bSchedule = false
            }
            else if (logging) log.info "not taking action because nothing is scheduled for the back door"
        }
    }
    else if (logging) log.info "not taking action because mode is $cMode"
}
    
def bLocking() {
   if(bLock.currentLock.contains("unlocked")){
        if(bContact.currentContact.contains("open")){
            if (logging) log.warn "Back Door conctact is open, checking again in one minute"
            runIn(1 * 60, secondTryB)
        }
       else {
           state.bSchedule = false
           if (logging) log.info "locking the back door"
           state.bSchedule = false
           bLock.lock()
       }
    }
    else if (logging) log.warn "not taking action because back door lock is ${fLock.currentLock}"
}
    
def secondTryB() {
    if (bLock.currentLock.contains("unlocked")) {
       if (bContact.currentContact.contains("closed")){
           if (logging) log.info "locking the back door after second try"
           state.bSchedule = false
           bLock.lock()
       }
       else {
            if (logging) log.warn "Back door conctact is still open checking again in one minute"
            runIn(1 * 60, thirdTryB)    
        }
    }
    else log.info "not taking action because back door lock is already ${bLock.currentLock}"
}

def thirdTryB() {
    if(bLock.currentLock.contains("unlocked")){
        log.error "locking the back door after third try, please check your contact sensor"
        def message = "Heads up, locking the back door while the door appears to be open" 
        state.bSchedule = false
        if (speakDev) playMessage(message)
        bLock.lock()
    }
}
     
private getModeOk() {
	def result = !modesY || modesY.contains(location.mode)
//	log.trace "modeYOk = $result"
	return result
}
def playMessage(message) {
        def track = "http://soundbible.com/mp3/Electronic_Chime-KevanGC-495939803.mp3"
        if (logging) log.debug "Sending message to selected speakers"
		if (introS) speakDev.each { it.playTrack(track) }
		speakDev.each { it.speak(message) }
}
