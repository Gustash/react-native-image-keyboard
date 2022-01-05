import 'react-native';

export interface onImageChangeEvent {
    uri : string;
    linkUri : string;
    mime : string;
    data? : string;
}

declare module 'react-native' {
    
    interface TextInputProps {
        onImageChange: (event : onImageChangeEvent) => void;
    }

}