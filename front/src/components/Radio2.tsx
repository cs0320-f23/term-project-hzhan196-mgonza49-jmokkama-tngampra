import * as React from "react";
import Radio from "@mui/material/Radio";
import RadioGroup from "@mui/material/RadioGroup";
import FormControlLabel from "@mui/material/FormControlLabel";
import FormControl from "@mui/material/FormControl";
import Typography from "@mui/material/Typography";
import { Field, useField } from "formik";

export default function RowRadioButtonsGroup({
  first,
  last,
  name,
}: {
  first: string;
  last: string;
  name: string;
}) {
  const [field, , helpers] = useField<string>(name);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement>,
    value: string
  ) => {
    helpers.setValue(value);
  };
  return (
    <FormControl
      style={{
        display: "flex",
        flexDirection: "row",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      {/* <FormLabel id="demo-row-radio-buttons-group-label">How Accepting Communities Are</FormLabel> */}
      <Typography component="legend">{first} </Typography>
      <Field name={name}>
        {({
          field,
        }: {
          field: {
            value: string;
          };
        }) => (
          <RadioGroup
            row
            style={{ marginLeft: "3vh" }}
            value={field.value}
            onChange={handleChange}
          >
            <FormControlLabel
              value="1"
              control={<Radio style={{ color: "white" }} />}
              label=""
            />
            <FormControlLabel
              value="2"
              control={<Radio style={{ color: "white" }} />}
              label=""
            />
            <FormControlLabel
              value="3"
              control={<Radio style={{ color: "white" }} />}
              label=""
            />
            <FormControlLabel
              value="4"
              control={<Radio style={{ color: "white" }} />}
              label=""
            />
          </RadioGroup>
        )}
      </Field>
      <Typography component="legend">{last} </Typography>
    </FormControl>
  );
}
