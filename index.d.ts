import { TextInputProps as RNTextInputProps } from "react-native";
declare module "react-native" {
  export type ImageChangeEventData = {
    uri: string;
    data: string;
    linkUri?: string;
    mime?: string;
  };
  export type ImageChangeEvent = NativeSyntheticEvent<ImageChangeEventData>;

  export interface TextInputProps extends RNTextInputProps {
    onImageChange?(event: ImageChangeEvent): void;
  }
}
