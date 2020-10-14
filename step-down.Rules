definition(
	name: "Step-down Rules",
	singleInstance: true,
	namespace: "BD",
	author: "Bobby Dobrescu",
	description: "Create step-down rules to alert family members that lights are about to turn off",
	category: "Convenience",
	iconUrl: "",
	iconX2Url: "",
	installOnOpen: true
)

preferences {
	page(name: "mainPage")
  	page(name: "removePage")
}

def mainPage() {
	dynamicPage(name: "mainPage", title: " ", install: true, uninstall: false) {
		section {
			app(name: "childButtons", appName: "Step-Down Rule", namespace: "BD", title: "Create a new Step-down Rule", multiple: true, displayChildApps: false)
			paragraph("  ")
			href "removePage", title: "Remove Step-down Rules", description: ""
		}
	}
}

def removePage() {
	dynamicPage(name: "removePage", title: "Remove all Step-down Rules", install: false, uninstall: true) {
		section ("WARNING!\n\nRemoving  removes all Step-down Rules\n") {
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
	createLocationVariable("SDRruleList", ruleList(), true)

}


def removeChild(ch) {
}
