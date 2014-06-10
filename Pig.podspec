Pod::Spec.new do |s|

  s.name         = "Pig"
  s.version      = "0.2.0"
  s.summary      = "Pig aims to be the bridge in the middle of your native mobile UI and a some shared JavaScript business logic."
  s.homepage     = "http://github.com/ribot/pig"

  s.description  = <<-DESC
                   Developing for multiple mobile platforms is both time-consuming and expensive. Writing completely separate
                   versions in Java and Objective-C in parallel duplicates logic and wastes effort, especially when requirements
                   change. Writing an application using PhoneGap generally leads to the UI to feeling non-standard and ultimately
                   to a sub-par user experience.

                   Pig aims to solves these problems by moving the business logic into a shared Javascript codebase. On each platform
                   (currently Android and iOS), the user interface is implemented in native code, leading the best possible user
                   experience, while data manipulation and other logic is handled by the shared Javascript.
                   DESC

  s.license            = { :type => "Apache License, Version 2.0", :file => "LICENSE" }
  s.author             = { "Matt Oakes" => "matt@ribot.co.uk" }
  s.social_media_url   = "http://twitter.com/ribot"

  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/ribot/pig.git", :tag => "0.2.0" }
  s.source_files  = "ios/Pig/Pig", "ios/Pig/Pig/**/*.{h,m}"
  s.public_header_files = "ios/Pig/Pig/Pig.h"
  s.resources = "**/bridge.js"
  s.requires_arc = true

end
