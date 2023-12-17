import * as React from "react";
import Checkbox from "@mui/material/Checkbox";
import TextField from "@mui/material/TextField";
import Autocomplete from "@mui/material/Autocomplete";
import CheckBoxOutlineBlankIcon from "@mui/icons-material/CheckBoxOutlineBlank";
import CheckBoxIcon from "@mui/icons-material/CheckBox";
import { Field } from "formik";

const icon = <CheckBoxOutlineBlankIcon fontSize="small" />;
const checkedIcon = <CheckBoxIcon fontSize="small" />;

export default function CheckboxesTags({
  data,
  placeholder,
  name,
  onChange,
}: {
  data: string[];
  placeholder: string;
  name: string;
  onChange: (selectedValues: string[]) => void;
}) {
  function handleChange(
    event: React.ChangeEvent<{}>,
    selectedOptions: string[]
  ) {
    onChange(selectedOptions);
  }
  return (
    <Autocomplete
      multiple
      id="checkboxes-tags-demo"
      options={data}
      disableCloseOnSelect
      getOptionLabel={(option) => option}
      onChange={handleChange}
      renderOption={(props, option, { selected }) => (
        <li {...props}>
          <Checkbox
            icon={icon}
            checkedIcon={checkedIcon}
            style={{ marginRight: 8, color: "gray" }}
            checked={selected}
          />
          {option}
        </li>
      )}
      style={{
        width: 500,
        height: 50,
        backgroundColor: "white",
        borderRadius: 8,
      }}
      renderInput={(params) => (
        <TextField {...params} label="" placeholder={placeholder} />
      )}
    />
  );
}
