
import { NativeSyntheticEvent } from "react-native";
declare module "react-native" {
  type ImageChangeEventData = {
    uri: string;
    data: string;
    linkUri?: string;
    mime?: string;
  };
  type ImageChangeEvent = NativeSyntheticEvent<ImageChangeEventData>;

  interface TextInputProps {
    onImageChange?: (event: ImageChangeEvent) => void;
  }
}
