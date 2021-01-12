require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-image-keyboard"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  react-native-media-input
                   DESC
  s.homepage     = "https://github.com/Gustash/react-native-image-keyboard"
  s.license      = { :type => "GPLv3", :file => "LICENSE" }
  s.authors      = { "Gustavo Parreira" => "gustavotcparreira@gmail.com" }
  s.platforms    = { :ios => "9.0" }
  s.source       = { :git => "https://github.com/Gustash/react-native-image-keyboard.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,swift}"
  s.requires_arc = true

  s.dependency "React"
  # ...
  # s.dependency "..."
end

